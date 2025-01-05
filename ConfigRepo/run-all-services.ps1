# ConfigServer'ı çalıştırma komutu
cd D:\WWW\Yazilim\spring\ConfigServer
Start-Process cmd -ArgumentList "/k mvn spring-boot:run"

# EurekaServer'ı çalıştırma komutu
cd D:\WWW\Yazilim\spring\EurekaServer
Start-Process cmd -ArgumentList "/k mvn spring-boot:run"

# 20 saniye bekle
Start-Sleep -Seconds 20

# ProductService instance1'i çalıştırma komutu
cd D:\WWW\Yazilim\spring\ProductService
Start-Process cmd -ArgumentList "/k mvn spring-boot:run"

# ProductService instance2'yi çalıştırma komutu
cd D:\WWW\Yazilim\spring\ProductService
Start-Process cmd -ArgumentList "/k mvn spring-boot:run"

# OrderService instance1'i çalıştırma komutu
cd D:\WWW\Yazilim\spring\OrderService
Start-Process cmd -ArgumentList "/k mvn spring-boot:run"

# OrderService instance2'yi çalıştırma komutu
cd D:\WWW\Yazilim\spring\OrderService
Start-Process cmd -ArgumentList "/k mvn spring-boot:run"
