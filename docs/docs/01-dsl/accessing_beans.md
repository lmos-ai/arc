---
title: Accessing Beans
sidebar_position: 2
---

Accessing other components or beans in the Arc DSL is vital for building complex Agents.
This can simply be done by using the `get` function. 

The `get` function requires 
a type and uses that type to lookup the your bean.

For example,
```kts
val myBean = get<MyBean>()
```
The `get` function can be called anywhere within the DSL.

The class that provides the beans is the `BeanProvider`.

```kts
interface BeanProvider {

    suspend fun <T : Any> provide(bean: KClass<T>): T
}
```


You are free to implement the `BeanProvider` interface to suit your needs. Or use the
`CoroutineBeanProvider` which is provided by the framework.

The `CoroutineBeanProvider` allows beans to be set in the Coroutine Context. 
The Coroutine Context is similar to the Java Thread Local mechanism for coroutines.
Meaning that beans are only available in a fix scope.

```kts
 val beanProvider = CoroutineBeanProvider()
 beanProvider.setContext(setOf(MyBean())) { 
     // somewhere in the DSL
     val myBean = get<MyBean>()
 }
 val myBean = get<MyBean>() // Bean is not available here
```

The `CoroutineBeanProvider` accepts a `fallbackBeanProvider`, 
that is used to lookup beans that are not found in the Coroutine Context.

```kts
val beanProvider = CoroutineBeanProvider(fallbackBeanProvider)
```

:::info Requirements
When using the `arc-spring-boot-starter`, `CoroutineBeanProvider` is automatically 
configured as a default and connects to the Spring Boot container.
Allowing the DSL to access any bean defined in the Spring Boot container.
:::