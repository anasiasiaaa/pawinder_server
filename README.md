# Pawinder Server

Серверная часть мобильного приложения Pawinder (подбор животных из приюта). Отдаёт клиенту список питомцев по REST (JSON), получает карточки животных из TheCatAPI и TheDogAPI, хранит данные в MySQL-совместимой базе.

Клиент (Android): https://github.com/viruy04/Pawinder_1.1

## Стек

| Что | Чем |
|---|---|
| Язык и версия | Java 17 (проверено также на более новых JDK) |
| Фреймворк | Spring Boot 4.1.1 (Web MVC, Data JPA, Validation) |
| Сборка и зависимости | Maven (Maven Wrapper `mvnw`, версии указаны в `pom.xml`) |
| База данных | MySQL 8 (драйвер `mysql-connector-j`), в облаке TiDB Cloud |
| Тесты | JUnit 5, Mockito, Spring Boot Test; покрытие JaCoCo; запуск `mvnw verify` |
| Внешние API | TheCatAPI, TheDogAPI (нужны личные ключи) |

## Как сервер запускается у нас

Рабочая версия развёрнута в облаке: приложение на Render, база данных на TiDB Cloud. Адреса, пароли и ключи в репозиторий не входят, они задаются настройками окружения (в Render: раздел Environment). Для проверки и разработки сервер можно запустить локально с локальной MySQL (разделы ниже), без доступа к нашим облачным учётным записям.

## Предварительные условия для локального запуска

- Git.
- JDK 17 или новее (например, Temurin 17), переменная `JAVA_HOME` указывает на него. Проверка: `java -version`.
- MySQL 8 на этом же компьютере (MySQL Installer + MySQL Workbench) **или** Docker.
- Личные бесплатные ключи TheCatAPI (thecatapi.com) и TheDogAPI (thedogapi.com).
- Доступ в интернет к Maven Central (скачивание зависимостей при первой сборке).

Больше ничего ставить не нужно: Maven скачивается через `mvnw`.

## Получить код

```bash
git clone https://github.com/anasiasiaaa/pawinder_server.git
cd pawinder_server
git rev-parse --short HEAD        # записать commit проверяемой версии
```

## Подготовить базу данных

В папке `db/` лежат скрипты: `01-schema.sql` (таблицы) и `02-reference-data.sql` (справочник типов «Кошка», «Собака»). Они рассчитаны на пустую базу `pawinder` и ничего не удаляют.

Вариант с Docker: скрипты выполняются автоматически при первом запуске контейнера (пароль придумайте сами и не коммитьте его).

```bash
# Linux и macOS
docker run --name pawinder-mysql -e MYSQL_ROOT_PASSWORD=<пароль> -e MYSQL_DATABASE=pawinder -p 3306:3306 -v "$(pwd)/db:/docker-entrypoint-initdb.d" -d mysql:8
```

```powershell
# Windows (PowerShell)
docker run --name pawinder-mysql -e MYSQL_ROOT_PASSWORD=<пароль> -e MYSQL_DATABASE=pawinder -p 3306:3306 -v "${PWD}/db:/docker-entrypoint-initdb.d" -d mysql:8
```

Дождитесь готовности MySQL: docker logs pawinder-mysql должен содержать строку ready for connections с port: 3306 (первая такая строка без порта относится к временному серверу инициализации). После этого убедитесь, что таблицы созданы:

```bash
docker exec pawinder-mysql mysql -uroot -p<пароль> pawinder -e "SHOW TABLES; SELECT * FROM type;"
```

Ожидается 12 таблиц и две строки в `type`.

Вариант без Docker: в MySQL Workbench создайте базу (`CREATE DATABASE pawinder CHARACTER SET utf8mb4;`) и выполните по очереди `db/01-schema.sql` и `db/02-reference-data.sql` (File → Run SQL Script). Из командной строки: `mysql -uroot -p --default-character-set=utf8mb4 pawinder < db/01-schema.sql`, то же для второго файла.

## Настройки (переменные окружения)

Настройки задаются переменными окружения, пароли и ключи в код и в репозиторий не записываются.

| Переменная | Назначение | Пример значения |
|---|---|---|
| `SPRING_DATASOURCE_URL` | адрес базы данных | `jdbc:mysql://localhost:3306/pawinder` |
| `SPRING_DATASOURCE_USERNAME` | пользователь базы данных | `root` |
| `SPRING_DATASOURCE_PASSWORD` | пароль (не публиковать) | `<ваш пароль>` |
| `CATAPI_KEY` | ключ TheCatAPI (обязателен) | `<ваш ключ>` |
| `DOGAPI_KEY` | ключ TheDogAPI (обязателен) | `<ваш ключ>` |
| `PORT` | порт сервера, по умолчанию 8080 | `8080` |

Если `CATAPI_KEY` или `DOGAPI_KEY` не заданы или пусты, сервер не запустится и сообщит, какая переменная пропущена.

Пример для PowerShell:

```powershell
$env:SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/pawinder"
$env:SPRING_DATASOURCE_USERNAME="root"
$env:SPRING_DATASOURCE_PASSWORD="<ваш пароль>"
$env:CATAPI_KEY="<ваш ключ>"
$env:DOGAPI_KEY="<ваш ключ>"
```

Пример для Linux и macOS:

```bash
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/pawinder
export SPRING_DATASOURCE_USERNAME=root
export SPRING_DATASOURCE_PASSWORD=<ваш пароль>
export CATAPI_KEY=<ваш ключ>
export DOGAPI_KEY=<ваш ключ>
```

## Сборка и тесты

```powershell
.\mvnw.cmd -B verify        # Windows
./mvnw -B verify            # Linux и macOS
```

Если на Linux или macOS появилось `Permission denied`, выполните `chmod +x mvnw`.

Ожидается: `BUILD SUCCESS`, `Failures: 0, Errors: 0`. Тесты не требуют базы данных и сети. Отчёт покрытия: `target/site/jacoco/index.html`.

## Запуск

Режим разработки:

```powershell
.\mvnw.cmd spring-boot:run
```

Режим поставки (собранный jar, запуск из любого каталога):

```bash
./mvnw -B -DskipTests package
java -jar target/pawinder_server-0.0.1-SNAPSHOT.jar
```

Ожидается в журнале строка `Started PawinderServerApplication`, а также диагностические строки:

```
Pawinder server started: version=0.0.1-SNAPSHOT+<sha>, runMode=classes
Database host=localhost:3306
catapi.key set=true, dogapi.key set=true
```

`runMode` равен `classes` при `spring-boot:run` и `jar` при запуске jar-файла.

## Проверка, что сервер работает

Версия (должна совпадать с `git rev-parse --short HEAD`; суффикс `-dirty` означает незакоммиченные изменения):

```powershell
curl http://localhost:8080/api/version
```

Загрузка животных в базу и список:

```powershell
curl -X POST "http://localhost:8080/api/pets/sync/cats?count=5"
curl -X POST "http://localhost:8080/api/pets/sync/dogs?count=5"
curl "http://localhost:8080/api/pets?page=0&size=5"
```

Проверка развёрнутого в облаке сервера (без установки чего-либо):

```powershell
curl https://pawinder-server.onrender.com/api/version
curl "https://pawinder-server.onrender.com/api/pets?page=0&size=5"
```

На бесплатном тарифе Render сервис «засыпает», первый запрос может занять около минуты.

## Остановка, повторный запуск, исходное состояние

- Остановка: `Ctrl+C` в окне, где запущен сервер.
- Повторный запуск: снова `.\mvnw.cmd spring-boot:run`, результат тот же.
- Вернуть базу в исходное состояние: `docker rm -f pawinder-mysql` и повторить подготовку базы данных (скрипты из `db/` выполнятся заново). Без Docker: `DROP DATABASE pawinder; CREATE DATABASE pawinder CHARACTER SET utf8mb4;` и снова выполнить оба скрипта.

## Частые ошибки

| Симптом | Причина и решение |
|---|---|
| `./mvnw: Permission denied` | нет права на исполнение: `chmod +x mvnw` |
| Сервер сразу завершается с сообщением про `CATAPI_KEY` или `DOGAPI_KEY` | переменная не задана или пуста |
| `Communications link failure` | БД не запущена или не готова, проверьте `SPRING_DATASOURCE_URL` |
| `Table 'pawinder.pet' doesn't exist` | не выполнен `db/01-schema.sql` |
| `Тип 'Кошка' не найден в справочнике type` | не выполнен `db/02-reference-data.sql` |
| В версии `unknown` | код получен без каталога `.git` (архив вместо `git clone`) |

## Безопасность

- Адрес базы, имя пользователя, пароль и ключи API берутся только из переменных окружения; файла с реальными значениями в репозитории нет.
- Пароль базы и значения ключей в журнал не выводятся.
- Облачные панели Render и TiDB Cloud доступны только участникам проекта и для проверки не нужны.

## Автоматические проверки

Файл `.github/workflows/` – конвейер GitHub Actions запускает сборку и тесты при изменениях.
