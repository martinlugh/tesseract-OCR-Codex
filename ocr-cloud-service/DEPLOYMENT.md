# Linux 服务器部署手册

## 1. 环境要求
- Linux x86_64
- Java 17+
- Tesseract 5+
- chi_sim、eng 语言包

## 2. 安装命令（Ubuntu/Debian）
```bash
sudo apt-get update
sudo apt-get install -y openjdk-17-jre tesseract-ocr tesseract-ocr-chi-sim tesseract-ocr-eng
```

## 3. 验证命令
```bash
java -version
tesseract --version
ls /usr/share/tesseract-ocr/5/tessdata | grep -E 'chi_sim|eng'
```

## 4. 配置建议
- `ocr.engine.datapath=/usr/share/tesseract-ocr/5/tessdata`
- `ocr.engine.language=chi_sim+eng`
- `ocr.engine.psm=3`
- `ocr.engine.oem=1`

## 5. 启动
```bash
mvn -DskipTests package
java -jar target/ocr-cloud-service-1.0.0.jar
```

## 6. 运维检查
```bash
curl http://127.0.0.1:8080/actuator/health
curl http://127.0.0.1:8080/api/ocr/health
```
