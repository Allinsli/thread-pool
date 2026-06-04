#Thread Pool#

Курсовая работа по дисциплине «Многопоточное и асинхронное программирование на Java».

#Содержание#

В проекте реализован собственный пул потоков с поддержкой:

corePoolSize и maxPoolSize;
keepAliveTime;
ограничения размера очередей;
minSpareThreads;
распределения задач по алгоритму Round Robin;
обработки перегрузки через RejectedTaskHandler;
логирования основных событий работы пула.

#Структура проекта#

src/main/java/org/example/threadpool/

CustomExecutor.java — интерфейс пула потоков.
CustomThreadPool.java — основная реализация пула.
Worker.java — рабочий поток.
CustomThreadFactory.java — создание потоков с уникальными именами.
RejectedTaskHandler.java — интерфейс обработки отказов.
AbortRejectedTaskHandler.java — реализация политики отказа.
NamedTask.java — демонстрационная задача.
Main.java — демонстрация работы приложения.

#Особенности реализации#

Для распределения задач используется алгоритм Round Robin.

Каждый Worker имеет собственную очередь задач на основе BlockingQueue.

При достижении максимальной нагрузки новые задачи отклоняются с использованием AbortRejectedTaskHandler.

#Запуск проекта#

Открыть проект в IntelliJ IDEA.
Использовать JDK 17.
Запустить класс Main.

#Автор#

ФИО: Хлоповских А.Ю.