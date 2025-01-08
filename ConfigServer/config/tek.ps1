# ConfigServer'ı çalıştırma komutu
cd D:\WWW\Yazilim\spring\Spring\ConfigServer
Start-Process cmd -ArgumentList "/k mvn spring-boot:run"

Start-Sleep -Seconds 30

# EurekaServer'ı çalıştırma komutu
cd D:\WWW\Yazilim\spring\Spring\EurekaServer
Start-Process cmd -ArgumentList "/k mvn spring-boot:run"

# 20 saniye bekle
Start-Sleep -Seconds 30

# ProductService instance1'i çalıştırma komutu
cd D:\WWW\Yazilim\spring\Spring\ProductService
Start-Process cmd -ArgumentList "/k mvn spring-boot:run"

# OrderService instance1'i çalıştırma komutu
cd D:\WWW\Yazilim\spring\Spring\OrderService
Start-Process cmd -ArgumentList "/k mvn spring-boot:run"
