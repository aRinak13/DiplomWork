## Автоматизация приложения покупки туров

### Сценарии тестирования формы покупки тура по дебетовой карте

1. Успешная покупка тура через оплату дебетовой картой с заполнением полей валидными данными
2. Отказ в покупке тура через оплату дебетовой картой при не валидно заполненных данных
3. Отказ в покупке тура в случае ввода текущего года, но месяц вводится следующий за текущим
4. Валидация поля "Номер карты"
   * Ввод 15 цифр
   * Ввод 16 цифр
   * Ввод символов
   * Ввод латинских букв
   * Ввод русских букв
   * Обязательность поля
5. Валидация поля "Месяц"
   * Ввод 1 цифры
   * Ввод 3 цифр
   * Ввод 00
   * Ввод 01
   * Ввод 06
   * Ввод 12
   * Ввод 13
   * Ввод символов
   * Ввод латинских букв
   * Ввод русских букв
   * Обязательность поля
6. Валидация поля "Год"
   * Ввод 1 цифр
   * Ввод 3 цифр
   * Ввод года ниже текущего
   * Ввод символов
   * Ввод латинских букв
   * Ввод русских букв
   * Обязательность поля
7. Валидация поля "Владелец"
   * Ввод цифр
   * Ввод символов
   * Ввод латинских букв
   * Ввод русских букв
   * Обязательность поля
8. Валидация поля "CVC/CVV"
   * Ввод 2 цифр
   * Ввод 4 цифр
   * Ввод символов
   * Ввод латинских букв
   * Ввод русских букв
   * Обязательность поля

### Сценарии тестирования формы покупки тура в кредит

1. Успешная покупка тура через кредит с заполнением полей валидными данными
2. Отказ в покупке тура через кредит при не валидно заполненных данных
3. Отказ в покупке тура в случае ввода текущего года, но месяц вводится следующий за текущим
4. Валидация поля "Номер карты"
   * Ввод 15 цифр
   * Ввод 16 цифр
   * Ввод символов
   * Ввод латинских букв
   * Ввод русских букв
   * Обязательность поля
5. Валидация поля "Месяц"
   * Ввод 1 цифры
   * Ввод 3 цифр
   * Ввод 00
   * Ввод 01
   * Ввод 06
   * Ввод 12
   * Ввод 13
   * Ввод символов
   * Ввод латинских букв
   * Ввод русских букв
   * Обязательность поля
6. Валидация поля "Год"
   * Ввод 1 цифр
   * Ввод 3 цифр
   * Ввод года ниже текущего
   * Ввод символов
   * Ввод латинских букв
   * Ввод русских букв
   * Обязательность поля
7. Валидация поля "Владелец"
   * Ввод цифр
   * Ввод символов
   * Ввод латинских букв
   * Ввод русских букв
   * Обязательность поля
8. Валидация поля "CVC/CVV"
   * Ввод 2 цифр
   * Ввод 4 цифр
   * Ввод символов
   * Ввод латинских букв
   * Ввод русских букв
   * Обязательность поля
   
## Перечень используемых инструментов с обоснованием выбора

1. **IDE:** IntelliJ IDEA
2. **Язык программирования:** Java
3. **Система сборки:** Gradle - Настройка Gradle комфортней чем Maven
4. **Тестовая среда:** JUnit 5 - Удобнее и понятней для автоматизации
5. **Система репортинга:** Allure
6. **Фреймворки тестирования:**
   * Selenide - Простой инструмент для автоматизации тестов, 
   * REST Assured - Удобный инструмент для отправки REST запросов, 
   * Gson - фреймворк для генерации body запросов при API тестирования
   * Java Faker - удобный инструмент для генерации тестовых данных
   * Lombok - фреймворк для автогенерации кода с целью улучшить читаемость тестов

## Перечень и описание возможных рисков при автоматизации

1. Тестирование проходит для формы покупки, т.к. в реальной жизни данные карт у каждого покупателя разные, в тестах необходимо генерировать рандомные данные,  а не хардкодить их.
2. Из-за требование совместимости с двумя БД: MySQL, PostgreSQL, могут возникнут проблемы с разностью диалектов SQL
3. Из-за требование совместимости с двумя БД: MySQL, PostgreSQL, создается дополнительная трудность с настройкой SUT
4. Поскольку в качестве банковского сервиса выступает заглушка, на продуктивной среде могут появятся новые не выявленные дефекты
5. После каждого прогона нужно чистить БД

## Интервальная оценка с учётом рисков (в часах)

1. Настройка SUT, подключени CI и БД: 6 - 9 часов
2. Написание автотестов: 14 - 16 часов
3. Создание баг-репортов и отчёта по результатам прогона тестов: 4 - 6 часов
4. Отчёт по результатам автоматизации: 3 - 4 часа
   Всего: 27 - 35 часов

## План сдачи работ (когда будут авто-тесты, результаты их прогона и отчёт по автоматизации)

1. Авто-тесты будут готовы 11.11.2022
2. Баг-репорты и отчёт по результатам тестов: 18.11.2022
3. Отчёт по результатам автоматизации: 22.11.2022