# MeteoData

Для запуска требуется Java 26.

Скопируйте `config.properties.example` в `config.properties` в корне проекта
и укажите свой ключ в `yandex.weather.key`. Файл `config.properties` исключён
из Git; файл с примером можно публиковать.

Запускайте `org.example.Main` с рабочей директорией, равной корню проекта.
Программа запрашивает прогноз Яндекс Погоды и выводит JSON в консоль.
