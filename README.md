# Pawinder Server

Серверная часть мобильного приложения Pawinder (подбор животных из приюта). Отдаёт клиенту список питомцев и данные пользователей по REST (JSON), хранит данные в MySQL.

Клиент (Android): https://github.com/viruy04/Pawinder_1.1

## Стек

| Что | Чем |
|---|---|
| Язык и версия | Java 17 |
| Фреймворк | Spring Boot 4.1.1 (Web MVC, Data JPA) |
| Сборка и зависимости | Maven (Maven Wrapper `mvnw`, версии указаны в `pom.xml`) |
| База данных | MySQL (драйвер `mysql-connector-j`) |
| Тесты | JUnit через Spring Boot Test, запуск `mvnw verify` |

## Как сервер запускается у нас

Рабочая версия развёрнута в облаке: приложение – на Railway, база данных MySQL – на Aiven. Адреса и пароли базы в репозиторий не входят, они задаются настройками окружения. Для проверки и разработки сервер можно запустить локально с локальной MySQL (раздел ниже), без доступа к нашим облачным учётным записям.

## Предварительные условия для локального запуска

- Git.
- JDK 17 (например, Temurin 17), переменная `JAVA_HOME` указывает на него. Проверка: `java -version`.
- MySQL 8 на этом же компьютере (MySQL Installer + MySQL Workbench) **или** Docker.
- Доступ в интернет к Maven Central (скачивание зависимостей при первой сборке).

Больше ничего ставить не нужно: Maven скачивается через `mvnw`.

## Получить код

```bash
git clone https://github.com/anasiasiaaa/pawinder_server.git
cd pawinder_server
git rev-parse --short HEAD        # записать commit проверяемой версии
```

## Подготовить базу данных

Вариант с Docker (пароль придумайте сами и не коммитьте его):

```bash
docker run --name pawinder-mysql -e MYSQL_ROOT_PASSWORD=<пароль> -e MYSQL_DATABASE=pawinder -p 3306:3306 -d mysql:8
```

Вариант без Docker: в MySQL Workbench выполнить `CREATE DATABASE pawinder;`.

## Настройки (переменные окружения)

Настройки задаются переменными окружения, пароли в код и в репозиторий не записываются.

| Переменная | Назначение | Пример значения |
|---|---|---|
| `SPRING_DATASOURCE_URL` | адрес базы данных | `jdbc:mysql://localhost:3306/pawinder` |
| `SPRING_DATASOURCE_USERNAME` | пользователь базы данных | `root` |
| `SPRING_DATASOURCE_PASSWORD` | пароль (не публиковать) | `<ваш пароль>` |
| `PORT` | порт сервера, по умолчанию 8080 | `8080` |

Пример для PowerShell:

```powershell
$env:SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/pawinder"
$env:SPRING_DATASOURCE_USERNAME="root"
$env:SPRING_DATASOURCE_PASSWORD="<ваш пароль>"
```

## Сборка и тесты

```powershell
.\mvnw.cmd -B verify        # Windows
./mvnw -B verify            # Linux и macOS
```

Ожидается: `BUILD SUCCESS`.

## Запуск

```powershell
.\mvnw.cmd spring-boot:run
```

Ожидается в журнале строка `Started PawinderServerApplication`

## Проверка, что сервер работает

```powershell
curl http://localhost:8080/<путь списка питомцев> 
```

Проверка развёрнутого в облаке сервера (без установки чего-либо):

```powershell
curl https://pawinderserver-production.up.railway.app/<путь списка питомцев>
```

## Остановка, повторный запуск, исходное состояние

- Остановка: `Ctrl+C` в окне, где запущен сервер.
- Повторный запуск: снова `.\mvnw.cmd spring-boot:run`, результат тот же.
- Вернуть базу в исходное состояние: `DROP DATABASE pawinder; CREATE DATABASE pawinder;` и повторить подготовку базы данных.

## Безопасность

- Адрес базы, имя пользователя и пароль берутся только из переменных окружения; файла с реальными значениями в репозитории нет.
- Пароли пользователей в журнал не выводятся.
- Облачные панели Railway и Aiven доступны только участникам проекта и для проверки не нужны.

## Автоматические проверки

Файл `.github/workflows/` – конвейер GitHub Actions запускает сборку и тесты при изменениях.
