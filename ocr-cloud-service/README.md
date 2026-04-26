# OCR Cloud Service（Java 17 + Spring Boot 3）

## 1. 项目定位
本项目是可部署在自有服务器的本地 OCR 云服务：
- OCR 引擎使用本地 Tesseract（Tess4J 优先，CLI 回退）。
- Java 负责服务化、预处理、结构化抽取、日志追踪、健康检查与稳定性控制。
- 不依赖第三方在线 OCR 服务，不包含数据库。

## 2. 关键能力
- 图片 OCR（jpg/jpeg/png/bmp/tiff）。
- PDF OCR（单页/多页，PDFBox 渲染逐页识别）。
- 体检报告模板化结构化抽取（YAML 模板驱动，支持扩展）。
- Actuator 健康检查（含 OCR 引擎可用性）。
- traceId 链路日志、请求耗时、OCR 耗时、结构化抽取耗时日志。
- 文件大小限制、页数限制、任务超时、线程池并发控制、临时文件自动清理。

## 3. 本地启动
```bash
cd ocr-cloud-service
mvn spring-boot:run
```

## 4. Linux 服务器部署
### 4.1 安装 Java 17 与 Tesseract
```bash
sudo apt-get update
sudo apt-get install -y openjdk-17-jre tesseract-ocr tesseract-ocr-chi-sim tesseract-ocr-eng
```

### 4.2 检查 Tesseract 与语言包
```bash
tesseract --version
ls /usr/share/tesseract-ocr/5/tessdata | grep -E 'chi_sim|eng'
```

### 4.3 配置 tessdata 路径
`application.yml` 中配置：
```yml
ocr:
  engine:
    datapath: /usr/share/tesseract-ocr/5/tessdata
    language: chi_sim+eng
```

### 4.4 打包与运行
```bash
mvn -DskipTests package
java -jar target/ocr-cloud-service-1.0.0.jar
```

## 5. Docker 部署
```bash
docker compose up -d --build
```

## 6. 核心配置说明
- `ocr.engine.max-file-size-mb`：最大文件大小限制。
- `ocr.engine.max-pages`：最大 PDF 页数限制。
- `ocr.engine.task-timeout-seconds`：单任务超时。
- `ocr.engine.psm/oem/language`：Tesseract 推荐参数。
- `ocr.engine.executor-*`：并发线程池。
- `ocr.engine.temp-file-retention-minutes`：临时文件保留时长。

## 7. Actuator 健康检查
- `GET /actuator/health`
- 包含 `ocrEngine` 指标：datapath、tess4j、tesseractCli 可用性。

## 8. API 示例
### 8.1 图片 OCR
```bash
curl -X POST 'http://localhost:8080/api/ocr/image' \
  -H 'X-Trace-Id: demo-trace-001' \
  -F 'file=@src/test/resources/samples/demo-report.jpg' \
  -F 'language=chi_sim+eng'
```

### 8.2 PDF OCR
```bash
curl -X POST 'http://localhost:8080/api/ocr/pdf' \
  -H 'X-Trace-Id: demo-trace-002' \
  -F 'file=@src/test/resources/samples/demo-report.pdf' \
  -F 'language=chi_sim+eng'
```

### 8.3 健康检查
```bash
curl 'http://localhost:8080/actuator/health'
curl 'http://localhost:8080/api/ocr/health'
```

## 9. 返回 JSON 示例
```json
{
  "success": true,
  "code": "OK",
  "message": "请求成功",
  "data": {
    "fileName": "demo-report.pdf",
    "language": "chi_sim+eng",
    "engine": "tess4j+fallback",
    "fileType": "PDF",
    "totalPages": 2,
    "totalCostMs": 1520,
    "status": "SUCCESS",
    "text": "...原始OCR文本...",
    "structuredData": {
      "documentType": "HEALTH_CHECK_REPORT",
      "pages": [1, 2],
      "rawText": "...",
      "fields": [
        {
          "fieldCode": "GLU",
          "fieldName": "空腹血糖",
          "rawName": "空腹血糖",
          "value": "5.31",
          "unit": "mmol/L",
          "referenceRange": "3.9-6.1",
          "abnormalFlag": "",
          "confidence": 0.9,
          "sourcePage": 1,
          "sourceText": "空腹血糖:5.31 mmol/L 3.9-6.1"
        }
      ],
      "unmatchedLines": [],
      "warnings": []
    }
  }
}
```

## 10. 测试
- 单元测试：文本清洗、模板加载、字段抽取。
- 集成测试：健康接口可用性。
- 示例测试文件说明见 `src/test/resources/samples/README.md`。

## 11. 准确率保障策略
- 使用 Tesseract 官方能力：OEM/PSM/语言包配置。
- 针对拍照件执行预处理：旋转、灰度、去噪、二值化、倾斜校正。
- 体检报告采用模板化字段抽取，并可扩展模板。
- 保留原始 OCR 文本、字段来源行、字段置信度，支持人工复核。
- 对低置信度字段输出 warnings。
- 不承诺 100% 正确，但通过工程化手段提高稳定性与可复核性。

## 12. 常见部署问题排查
1. `Tesseract not found`：确认 `tesseract --version` 可执行，或配置 `ocr.engine.tesseract-command`。
2. `Failed loading language`：确认 `chi_sim.traineddata`、`eng.traineddata` 在 `datapath` 下。
3. `OCR_TIMEOUT`：调大 `task-timeout-seconds`，或缩小上传文件/页数。
4. `PDF_PAGE_LIMIT`：调大 `max-pages`。
5. 识别结果偏差：检查图像质量，调整 `psm/oem` 与模板规则。
