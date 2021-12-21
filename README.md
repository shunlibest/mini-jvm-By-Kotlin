# [Mini-jvm](https://jvm.guxingke.com) 
![CircleCI](https://img.shields.io/circleci/build/github/guxingke/mini-jvm/master?style=for-the-badge&token=f20bab2e6e06b66e96f9440f31fa391524a8ed60)![GitHub](https://img.shields.io/github/license/guxingke/mini-jvm?style=for-the-badge)![GitHub commit activity](https://img.shields.io/github/commit-activity/w/guxingke/mini-jvm?style=for-the-badge)![GitHub last commit](https://img.shields.io/github/last-commit/guxingke/mini-jvm?style=for-the-badge)
------
使用 Java 8 实现 JVM

<div align=center><img height="300px" width="300px" src="docs/assets/imgs/mini-jvm-logo.png"/></div>

## 和原版的区别
本项目主要是对原版的fork，再原有基础上，部分代码使用kotlin实现，不会改变代码流程（不会的可以学习一下，能够简化部分代码实现，把精力放在原理上）

## 特性

### 元循环(Metacircular)
mini-jvm on mini-jvm on hotspot. 可以在 mini-jvm 里运行 mini-jvm . 
```
$ java -jar jvm-core/target/mini-jvm.jar -jar jvm-core/target/mini-jvm.jar -jar test.jar
# Hello World!
```

## 动机

1. 尝试了解 JVM 原理, Learning by doing
2. 纸上得来终觉浅, 实践
3. 用简单的代码帮助 Javaer 理解 JVM

## 快速体验 [Macos 用户]

### Hello world

```bash
brew tap guxingke/repo && brew install mini-jvm

cat <<EOF > HelloWorld.java
public class HelloWorld {
  public static void main(String[] args) {
    if (args.length == 0) {
      System.out.println("hello");
      return;
    }

    for(int i = 0; i < args.length; i ++) {
      System.out.println(args[i]);
    }
  }
}
EOF

javac HelloWorld.java

# no args
mini-jvm HelloWold
# => hello

# with program args
mini-jvm HelloWold hello mini-jvm
# =>  hello
# =>  mini-jvm

# 输入 mini-jvm -help 了解更多.
```

## 快速体验 [其他操作系统]

需要自行下载打包. [Dev](https://jvm.guxingke.com/#/dev)

## 规划
- Class 文件解析 90%
- 字节码执行 90%
- 类加载 90%
- 方法调用 90%
- 实例化 90%
- Native 方法 90%
- 异常处理 60%
- self-booting 70%

## 局限
1. 不实现 GC
2. 不实现多线程

## 变更记录
- 实现了元循环(Metacircular)
- 反射特性基本可用
- 增加简单的调试器 bin/jdb.
- 支持 Lambda 调用, closure, currying 可用.
- Hello World 级别可用

## 个人记录
- [如何构建你自己的 JVM (2) Hello World](https://www.guxingke.com/post/how-to-build-your-own-jvm-3.html)
- [如何构建你自己的 JVM (1) 解释器](https://www.guxingke.com/post/how-to-build-your-own-jvm-2.html)
- [如何构建你自己的 JVM (0) 概述](https://www.guxingke.com/post/how-to-build-your-own-jvm-1.html)
- [用 Java 实现一个简单的虚拟机 ?](https://www.guxingke.com/post/mini-jvm-intro.html)

## 建议
- 在JDK1.8.0_121环境下发现编译不过的情况，详情见[#25](https://github.com/guxingke/mini-jvm/issues/25)，本项目的目的是学习JVM，为了项目足够小，清晰和易于理解，并不打算做各种适配工作，建议大家在MacOSX，Maven 3.3+,JDK 1.8.0_192+下学习；

## 联系作者
<details>
<summary>微信群</summary>
<p>加个人微信 `guxingke_`，备注 mini-jvm 拉你进群。</p>

<img style="margin:0 auto;display:block;" src='docs/assets/imgs/wechat.jpeg'></img>
</details>

## 参考
- [The Java® Virtual Machine Specification](https://docs.oracle.com/javase/specs/jvms/se8/html/)
- [JVM 字节码从入门到精通](https://juejin.im/book/5c25811a6fb9a049ec6b23ee/)
- [自己动手写Java虚拟机](https://book.douban.com/subject/26802084/)

