
1 今天我们多次用到了 ThreadLocalRandom，你觉得是否可以把它的实例设置到静态变量中，在多线程情况下重用呢？
2 ConcurrentHashMap 还提供了 putIfAbsent 方法，你能否通过查阅JDK 文档，说说 computeIfAbsent 和 putIfAbsent 方法的区别？


- [ ] 高级的锁工具类：可以在适当的场景下考虑使用 ReentrantReadWriteLock、StampedLock 等高级的锁工具类。
