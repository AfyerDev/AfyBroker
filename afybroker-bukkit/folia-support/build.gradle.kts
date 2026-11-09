// Folia 调度实现，打包进 afybroker-bukkit，只在 Folia 上使用；其他服务端不会加载本模块的类。
// folia-api 是 Java 17 字节码，构建需要 JDK 17；输出与其他模块一样是 Java 8 字节码（根项目统一 --release 8）。

java {
    // folia-api 声明需要 Java 17，本模块输出 Java 8，不按目标版本筛选依赖
    disableAutoTargetJvm()
}

dependencies {
    compileOnly(libs.folia.api)
}
