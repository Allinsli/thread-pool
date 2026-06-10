# Thread Pool

Курсовая работа по дисциплине «Многопоточное и асинхронное программирование на Java».

## Описание проекта

В рамках работы реализован собственный пул потоков без использования `ThreadPoolExecutor`. Пул поддерживает настройку
количества потоков, ограничение размера очередей задач, распределение нагрузки между потоками, обработку перегрузки и 
завершение неиспользуемых потоков по таймауту.

## Реализовано

* настройка `corePoolSize` и `maxPoolSize`;
* настройка `keepAliveTime`;
* ограничение размера очередей задач (`queueSize`);
* поддержка `minSpareThreads`;
* распределение задач по алгоритму Round Robin;
* обработка перегрузки через `RejectedTaskHandler`;
* логирование основных событий работы пула;
* поддержка методов `execute()`, `submit()`, `shutdown()` и `shutdownNow()`.

## Структура проекта

* `CustomExecutor.java` — интерфейс пула потоков;
* `CustomThreadPool.java` — реализация пула потоков;
* `Worker.java` — рабочий поток;
* `CustomThreadFactory.java` — фабрика потоков;
* `RejectedTaskHandler.java` — интерфейс обработки отказов;
* `AbortRejectedTaskHandler.java` — реализация политики отказа;
* `NamedTask.java` — демонстрационная задача;
* `Main.java` — демонстрация работы приложения.

## Балансировка задач

Для распределения задач используется алгоритм **Round Robin**.

Каждая новая задача помещается в следующую очередь по кругу. Для каждого Worker-потока используется собственная очередь
задач на основе `BlockingQueue`.
Такой подход позволяет достаточно равномерно распределять нагрузку между потоками и избегать ситуации, когда один поток 
перегружен значительно сильнее остальных.

## Политика отказа

В проекте используется обработчик `AbortRejectedTaskHandler`.

Если все очереди заполнены и количество потоков достигло значения `maxPoolSize`, новая задача отклоняется и 
генерируется исключение.

Преимущества выбранного подхода:

* простая реализация;
* предсказуемое поведение при перегрузке;
* отсутствие бесконтрольного роста потребления памяти.

Недостатки:

* часть задач может быть потеряна при высокой нагрузке;
* для повторного выполнения отклонённые задачи необходимо отправлять повторно.

## Анализ производительности

Для проверки работы пула были протестированы различные значения параметров `corePoolSize`, `maxPoolSize` и `queueSize`.

При использовании только базовых потоков (`corePoolSize = 2`, `maxPoolSize = 2`) время ожидания задач в очередях 
увеличивалось при высокой нагрузке, так как новые потоки не создавались.

Увеличение значения `maxPoolSize` до 4 позволило создавать дополнительные Worker-потоки и уменьшило время ожидания 
задач.

При небольшом размере очередей задачи чаще отклонялись из-за переполнения. Увеличение значения `queueSize` позволяло
принять больше задач, однако увеличивало время ожидания перед выполнением.

В ходе тестирования наиболее сбалансированными оказались следующие параметры:

* `corePoolSize = 2`;
* `maxPoolSize = 4`;
* `queueSize = 5`;
* `keepAliveTime = 5 секунд`.

При таких настройках пул эффективно обрабатывал задачи, создавал дополнительные потоки только при необходимости и 
корректно освобождал ресурсы после завершения нагрузки.

По сравнению со стандартным `ThreadPoolExecutor` реализованный пул содержит меньше встроенных оптимизаций и 
возможностей настройки. Однако собственная реализация позволяет лучше понять механизмы распределения задач между 
потоками, масштабирования пула и обработки перегрузки.

## Демонстрация работы

В классе `Main` продемонстрированы:

* выполнение задач через `execute()`;
* выполнение задач через `submit()` и получение результата через `Future`;
* создание дополнительных потоков при увеличении нагрузки;
* отклонение задач при перегрузке;
* корректное завершение работы через `shutdown()`.

## Запуск

1. Открыть проект в IntelliJ IDEA.
2. Использовать JDK 17.
3. Запустить класс `Main`.

## Пример вывода работы:

[ThreadFactory] Creating new thread: MyPool-worker-1
[ThreadFactory] Creating new thread: MyPool-worker-2
[Pool] Task accepted into queue #0: java.util.concurrent.FutureTask@312b1dae[Not completed, task = org.example.threadpool.Main$$Lambda$17/0x0000000800c02888@27973e9b]
[Worker] MyPool-worker-1 executes java.util.concurrent.FutureTask@312b1dae[Not completed, task = org.example.threadpool.Main$$Lambda$17/0x0000000800c02888@27973e9b]
[Main] Callable completed
[Pool] Task accepted into queue #1: Task-1
[Worker] MyPool-worker-2 executes Task-1
[Pool] Task accepted into queue #0: Task-2
[Worker] MyPool-worker-1 executes Task-2
[Pool] Task accepted into queue #1: Task-3
[Pool] Task accepted into queue #0: Task-4
[ThreadFactory] Creating new thread: MyPool-worker-3
[Pool] Task accepted into queue #2: Task-5
[ThreadFactory] Creating new thread: MyPool-worker-4
[Worker] MyPool-worker-3 executes Task-5
[Pool] Task accepted into queue #2: Task-6
[Pool] Task accepted into queue #3: Task-7
[Pool] Task accepted into queue #0: Task-8
[Worker] MyPool-worker-4 executes Task-7
[Pool] Task accepted into queue #1: Task-9
[Pool] Task accepted into queue #2: Task-10
[Pool] Task accepted into queue #3: Task-11
[Pool] Task accepted into queue #0: Task-12
[Pool] Task accepted into queue #1: Task-13
[Pool] Task accepted into queue #2: Task-14
[Pool] Task accepted into queue #3: Task-15
[Pool] Task accepted into queue #0: Task-16
[Pool] Task accepted into queue #1: Task-17
[Pool] Task accepted into queue #2: Task-18
[Pool] Task accepted into queue #3: Task-19
[Pool] Task accepted into queue #0: Task-20
[Pool] Task accepted into queue #1: Task-21
[Pool] Task accepted into queue #2: Task-22
[Pool] Task accepted into queue #3: Task-23
[Task] Task-7 started
[Task] Task-5 started
[Rejected] Task Task-24 was rejected due to overload!
[Task] Task-2 started
[Main] Task rejected: Task-24
[Rejected] Task Task-25 was rejected due to overload!
[Main] Task rejected: Task-25
[Rejected] Task Task-26 was rejected due to overload!
[Main] Task rejected: Task-26
[Pool] Task accepted into queue #3: Task-27
[Task] Task-1 started
[Rejected] Task Task-28 was rejected due to overload!
[Main] Task rejected: Task-28
[Rejected] Task Task-29 was rejected due to overload!
[Main] Task rejected: Task-29
[Rejected] Task Task-30 was rejected due to overload!
[Main] Task rejected: Task-30

## Автор

Хлоповских А.Ю.