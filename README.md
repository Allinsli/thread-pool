# Thread Pool

Курсовая работа по дисциплине «Многопоточное и асинхронное программирование на Java».

## Содержание

В проекте реализован собственный пул потоков с поддержкой:

* corePoolSize и maxPoolSize
* keepAliveTime
* ограничения размера очередей
* minSpareThreads
* распределения задач по алгоритму Round Robin
* обработки перегрузки через RejectedTaskHandler
* логирования основных событий работы пула

## Структура проекта

src/main/java/org/example/threadpool/

* CustomExecutor.java — интерфейс пула потоков
* CustomThreadPool.java — реализация пула
* Worker.java — рабочий поток
* CustomThreadFactory.java — фабрика потоков
* RejectedTaskHandler.java — интерфейс обработки отказов
* AbortRejectedTaskHandler.java — реализация политики отказа
* NamedTask.java — демонстрационная задача
* Main.java — демонстрация работы приложения

## Запуск проекта

1. Открыть проект в IntelliJ IDEA.
2. Использовать JDK 17.
3. Запустить класс Main.

## Автор

Хлоповских А.Ю.
