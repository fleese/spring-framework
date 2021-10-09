/*
 * Copyright 2002-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.beans.factory;

import org.springframework.beans.BeansException;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

/**
 * The root interface for accessing a Spring bean container.
 * 访问spring容器的根接口。
 *
 * <p>This is the basic client view of a bean container;
 * further interfaces such as {@link ListableBeanFactory} and
 * {@link org.springframework.beans.factory.config.ConfigurableBeanFactory}
 * are available for specific purposes.
 * 这是bean容器的一个基本视图，针对特定目的可使用ListableBeanFactory和ConfigurableBeanFactory。
 *
 * <p>This interface is implemented by objects that hold a number of bean definitions,
 * each uniquely identified by a String name. Depending on the bean definition,
 * the factory will return either an independent instance of a contained object
 * (the Prototype design pattern), or a single shared instance (a superior
 * alternative to the Singleton design pattern, in which the instance is a
 * singleton in the scope of the factory). Which type of instance will be returned
 * depends on the bean factory configuration: the API is the same. Since Spring
 * 2.0, further scopes are available depending on the concrete application
 * context (e.g. "request" and "session" scopes in a web environment).
 * 该接口由包含多个bean定义的对象实现，每个bean定义由String类型的名称唯一标识。
 * 依据bean定义，该接口实现将返回一个bean的独立实例（原型模式），
 * 或者单独的共享实例（这是Singleton设计模式的一个更好的替代方案，尽是工厂范围内的一个单例）。
 * 根据bean factory中的配置返回不同类型的bean实例（构建bean的API都是一样的）。
 * 从Spring 2.0开始，在一些应用场景中可以使用更加精准的bean 作用域可用，
 * 比如在web 应用环境下的"request"和"session"作用域。
 *
 *
 * <p>The point of this approach is that the BeanFactory is a central registry
 * of application components, and centralizes configuration of application
 * components (no more do individual objects need to read properties files,
 * for example). See chapters 4 and 11 of "Expert One-on-One J2EE Design and
 * Development" for a discussion of the benefits of this approach.
 * BeanFactory主要是一个应用程序组件的注册中心，并归集了应用程序组件的配置项，
 * 比如单个对象不用再亲自加载properties文件。
 *
 * <p>Note that it is generally better to rely on Dependency Injection
 * ("push" configuration) to configure application objects through setters
 * or constructors, rather than use any form of "pull" configuration like a
 * BeanFactory lookup. Spring's Dependency Injection functionality is
 * implemented using this BeanFactory interface and its subinterfaces.
 * 注意使用setter和constructor的push模式比使用像BeanFactory查找一样的pull模式更好。
 * Spring的依赖注入功能被BeanFactory接口及其子接口实现。
 *
 * <p>Normally a BeanFactory will load bean definitions stored in a configuration
 * source (such as an XML document), and use the {@code org.springframework.beans}
 * package to configure the beans. However, an implementation could simply return
 * Java objects it creates as necessary directly in Java code. There are no
 * constraints on how the definitions could be stored: LDAP, RDBMS, XML,
 * properties file, etc. Implementations are encouraged to support references
 * amongst beans (Dependency Injection).
 * 通常BeanFactory加载配置源（比如XML文档）中的bean定义，
 * 然后使用{@code org.springframework.beans}包配置bean对象。
 * 然而有时候仅在需要的时候才创建bean对象返回。
 * 对于如何存储bean定义没有限制,LDAP、RDBMS、XML、properties文件等都可以。
 * 支持bean之间的引用的接口实现是被鼓励的（注入依赖）。
 *
 *
 * <p>In contrast to the methods in {@link ListableBeanFactory}, all of the
 * operations in this interface will also check parent factories if this is a
 * {@link HierarchicalBeanFactory}. If a bean is not found in this factory instance,
 * the immediate parent factory will be asked. Beans in this factory instance
 * are supposed to override beans of the same name in any parent factory.
 * 与 {@link ListableBeanFactory}不同，在{@link HierarchicalBeanFactory}接口中的所有操作也将检查其父接口。
 * 如果bean未在接口实例中发现，其将检索直接父接口。
 * 这个工厂实例中的bean应该覆盖任何父工厂中同名的bean。
 *
 * <p>Bean factory implementations should support the standard bean lifecycle interfaces
 * as far as possible. The full set of initialization methods and their standard order is:
 * Bean factory 实现要尽可能的支持标准的bean生命周期接口。
 * <ol>
 * <li>BeanNameAware's {@code setBeanName}
 * <li>BeanClassLoaderAware's {@code setBeanClassLoader}
 * <li>BeanFactoryAware's {@code setBeanFactory}
 * <li>EnvironmentAware's {@code setEnvironment}
 * <li>EmbeddedValueResolverAware's {@code setEmbeddedValueResolver}
 * <li>ResourceLoaderAware's {@code setResourceLoader}
 * (only applicable when running in an application context)
 * <li>ApplicationEventPublisherAware's {@code setApplicationEventPublisher}
 * (only applicable when running in an application context)
 * <li>MessageSourceAware's {@code setMessageSource}
 * (only applicable when running in an application context)
 * <li>ApplicationContextAware's {@code setApplicationContext}
 * (only applicable when running in an application context)
 * <li>ServletContextAware's {@code setServletContext}
 * (only applicable when running in a web application context)
 * <li>{@code postProcessBeforeInitialization} methods of BeanPostProcessors
 * <li>InitializingBean's {@code afterPropertiesSet}
 * <li>a custom init-method definition
 * <li>{@code postProcessAfterInitialization} methods of BeanPostProcessors
 * </ol>
 *
 * <p>On shutdown of a bean factory, the following lifecycle methods apply:
 * <ol>
 * <li>{@code postProcessBeforeDestruction} methods of DestructionAwareBeanPostProcessors
 * <li>DisposableBean's {@code destroy}
 * <li>a custom destroy-method definition
 * </ol>
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 13 April 2001
 * @see BeanNameAware#setBeanName
 * @see BeanClassLoaderAware#setBeanClassLoader
 * @see BeanFactoryAware#setBeanFactory
 * @see org.springframework.context.ResourceLoaderAware#setResourceLoader
 * @see org.springframework.context.ApplicationEventPublisherAware#setApplicationEventPublisher
 * @see org.springframework.context.MessageSourceAware#setMessageSource
 * @see org.springframework.context.ApplicationContextAware#setApplicationContext
 * @see org.springframework.web.context.ServletContextAware#setServletContext
 * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessBeforeInitialization
 * @see InitializingBean#afterPropertiesSet
 * @see org.springframework.beans.factory.support.RootBeanDefinition#getInitMethodName
 * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessAfterInitialization
 * @see DisposableBean#destroy
 * @see org.springframework.beans.factory.support.RootBeanDefinition#getDestroyMethodName
 */
public interface BeanFactory {

	/**
	 * Used to dereference a {@link FactoryBean} instance and distinguish it from
	 * beans <i>created</i> by the FactoryBean. For example, if the bean named
	 * {@code myJndiObject} is a FactoryBean, getting {@code &myJndiObject}
	 * will return the factory, not the instance returned by the factory.
	 * 用于解除对{@link FactoryBean}实例的引用，区分被FactoryBean创造的bean。
	 * 比如一个FactoryBean的名称是{@code myJndiObject}，
	 * 使用{@code &myJndiObject}将返回该bean工厂，而不是bean工厂返回的实例。
	 */
	String FACTORY_BEAN_PREFIX = "&";


	/**
	 * Return an instance, which may be shared or independent, of the specified bean.
	 * 返回指定bean的一个实例，该实例可以是共享的，也可以是独立的。
	 * <p>This method allows a Spring BeanFactory to be used as a replacement for the
	 * Singleton or Prototype design pattern. Callers may retain references to
	 * returned objects in the case of Singleton beans.
	 * 该方法允许使用Spring BeanFactory来替代Singleton或Prototype设计模式。
	 * 在Singleton bean的情况下，调用者可能保留对返回对象的引用。
	 * <p>Translates aliases back to the corresponding canonical bean name.
	 * 将别名转换回相应的规范bean名称。
	 * <p>Will ask the parent factory if the bean cannot be found in this factory instance.
	 * 如何在当前工厂实例中找不到bean将询问父工厂。
	 * @param name the name of the bean to retrieve
	 * @return an instance of the bean
	 * @throws NoSuchBeanDefinitionException if there is no bean with the specified name
	 * @throws BeansException if the bean could not be obtained
	 */
	Object getBean(String name) throws BeansException;

	/**
	 * Return an instance, which may be shared or independent, of the specified bean.
	 * 返回指定bean的一个实例，该实例可以是共享的，也可以是独立的。
	 * <p>Behaves the same as {@link #getBean(String)}, but provides a measure of type
	 * safety by throwing a BeanNotOfRequiredTypeException if the bean is not of the
	 * required type. This means that ClassCastException can't be thrown on casting
	 * the result correctly, as can happen with {@link #getBean(String)}.
	 * 与{@link #getBean(String)}基本相同，但是提供了类型安全的校验，
	 * 当bean 工厂返回的bean不是要求的类型时抛出BeanNotOfRequiredTypeException。
	 * <p>Translates aliases back to the corresponding canonical bean name.
	 * 将别名转换回相应的规范bean名称。
	 * <p>Will ask the parent factory if the bean cannot be found in this factory instance.
	 * 如何在当前工厂实例中找不到bean将询问父工厂。
	 * @param name the name of the bean to retrieve
	 * @param requiredType type the bean must match; can be an interface or superclass
	 * @return an instance of the bean
	 * @throws NoSuchBeanDefinitionException if there is no such bean definition
	 * @throws BeanNotOfRequiredTypeException if the bean is not of the required type
	 * @throws BeansException if the bean could not be created
	 */
	<T> T getBean(String name, Class<T> requiredType) throws BeansException;

	/**
	 * Return an instance, which may be shared or independent, of the specified bean.
	 * 返回指定bean的一个实例，该实例可以是共享的，也可以是独立的。
	 * <p>Allows for specifying explicit constructor arguments / factory method arguments,
	 * overriding the specified default arguments (if any) in the bean definition.
	 * 允许指定显式构造函数参数/工厂方法参数，覆盖bean定义中指定的默认参数(如果有的话)
	 * @param name the name of the bean to retrieve
	 * @param args arguments to use when creating a bean instance using explicit arguments
	 * (only applied when creating a new instance as opposed to retrieving an existing one)
	 * @return an instance of the bean
	 * @throws NoSuchBeanDefinitionException if there is no such bean definition
	 * @throws BeanDefinitionStoreException if arguments have been given but
	 * the affected bean isn't a prototype
	 * @throws BeansException if the bean could not be created
	 * @since 2.5
	 */
	Object getBean(String name, Object... args) throws BeansException;

	/**
	 * Return the bean instance that uniquely matches the given object type, if any.
	 * 返回与给定对象类型(如果有的话)唯一匹配的bean实例。
	 * <p>This method goes into {@link ListableBeanFactory} by-type lookup territory
	 * but may also be translated into a conventional by-name lookup based on the name
	 * of the given type. For more extensive retrieval operations across sets of beans,
	 * use {@link ListableBeanFactory} and/or {@link BeanFactoryUtils}.
	 * 该方法在{@link ListableBeanFactory}中使用by-typ模式检索bean，但也可以基于类名按照by-name模式检索bean。
	 * 更多有关bean集合中的检索操作可查阅{@link ListableBeanFactory}和{@link BeanFactoryUtils}。
	 * @param requiredType type the bean must match; can be an interface or superclass
	 * @return an instance of the single bean matching the required type
	 * @throws NoSuchBeanDefinitionException if no bean of the given type was found
	 * @throws NoUniqueBeanDefinitionException if more than one bean of the given type was found
	 * @throws BeansException if the bean could not be created
	 * @since 3.0
	 * @see ListableBeanFactory
	 */
	<T> T getBean(Class<T> requiredType) throws BeansException;

	/**
	 * Return an instance, which may be shared or independent, of the specified bean.
	 * 返回指定bean的一个实例，该实例可以是共享的，也可以是独立的。
	 * <p>Allows for specifying explicit constructor arguments / factory method arguments,
	 * overriding the specified default arguments (if any) in the bean definition.
	 * 允许指定显式构造函数参数/工厂方法参数，覆盖bean定义中指定的默认参数(如果有的话)
	 * <p>This method goes into {@link ListableBeanFactory} by-type lookup territory
	 * but may also be translated into a conventional by-name lookup based on the name
	 * of the given type. For more extensive retrieval operations across sets of beans,
	 * use {@link ListableBeanFactory} and/or {@link BeanFactoryUtils}.
	 * 该方法在{@link ListableBeanFactory}中使用by-typ模式检索bean，但也可以基于类名按照by-name模式检索bean。
	 * 更多有关bean集合中的检索操作可查阅{@link ListableBeanFactory}和{@link BeanFactoryUtils}。
	 * @param requiredType type the bean must match; can be an interface or superclass
	 * @param args arguments to use when creating a bean instance using explicit arguments
	 * (only applied when creating a new instance as opposed to retrieving an existing one)
	 * @return an instance of the bean
	 * @throws NoSuchBeanDefinitionException if there is no such bean definition
	 * @throws BeanDefinitionStoreException if arguments have been given but
	 * the affected bean isn't a prototype
	 * @throws BeansException if the bean could not be created
	 * @since 4.1
	 */
	<T> T getBean(Class<T> requiredType, Object... args) throws BeansException;

	/**
	 * Return a provider for the specified bean, allowing for lazy on-demand retrieval
	 * of instances, including availability and uniqueness options.
	 * 返回指定bean的提供程序，允许延迟按需检索实例，包括可用性和惟一性选项。
	 * @param requiredType type the bean must match; can be an interface or superclass
	 * @return a corresponding provider handle
	 * @since 5.1
	 * @see #getBeanProvider(ResolvableType)
	 */
	<T> ObjectProvider<T> getBeanProvider(Class<T> requiredType);

	/**
	 * Return a provider for the specified bean, allowing for lazy on-demand retrieval
	 * of instances, including availability and uniqueness options.
	 * 返回指定bean的提供程序，允许延迟按需检索实例，包括可用性和惟一性选项。
	 * @param requiredType type the bean must match; can be a generic type declaration（可以是泛型声明）.
	 * Note that collection types are not supported here, in contrast to reflective
	 * injection points. For programmatically retrieving a list of beans matching a
	 * specific type, specify the actual bean type as an argument here and subsequently
	 * use {@link ObjectProvider#orderedStream()} or its lazy streaming/iteration options.
	 * 注意，与反射注入点不同，这里不支持集合类型。
	 * 要以编程方式检索与特定类型匹配的bean列表，请在此处指定实际bean类型作为参数，
	 * 然后使用{@link ObjectProvider#orderedStream()}或其延迟加载流/迭代选项。
	 * @return a corresponding provider handle
	 * @since 5.1
	 * @see ObjectProvider#iterator()
	 * @see ObjectProvider#stream()
	 * @see ObjectProvider#orderedStream()
	 */
	<T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType);

	/**
	 * Does this bean factory contain a bean definition or externally registered singleton
	 * instance with the given name?
	 *  这个bean工厂包含一个给定名称的bean定义或是外部注册的单例实例?
	 * <p>If the given name is an alias, it will be translated back to the corresponding
	 * canonical bean name.
	 *  如果给定的名称是别名，它将被翻译成相应的规范bean名称。
	 * <p>If this factory is hierarchical, will ask any parent factory if the bean cannot
	 * be found in this factory instance.
	 * 如果是hierarchical（层级）类型的bean工厂，在本工厂实例中查找无果后将询问父工厂。
	 * <p>If a bean definition or singleton instance matching the given name is found,
	 * this method will return {@code true} whether the named bean definition is concrete
	 * or abstract, lazy or eager, in scope or not. Therefore, note that a {@code true}
	 * return value from this method does not necessarily indicate that {@link #getBean}
	 * will be able to obtain an instance for the same name.
	 *  只要bean定义或者单例实例通过name查找到，这个方法就会返回 {@code true}，
	 *  无论该bean定义是否是抽像的，延迟加载的，在作用域内的。
	 *  因此即使该方法返回{@code true}，也不意味着使用{@link #getBean}将返回相同名称的bean实例。
	 * @param name the name of the bean to query
	 * @return whether a bean with the given name is present
	 */
	boolean containsBean(String name);

	/**
	 * Is this bean a shared singleton? That is, will {@link #getBean} always
	 * return the same instance?
	 *  bean是否是可共享的单例模式实例？ {@link #getBean}方法是否总是返回相同的bean实例。
	 * <p>Note: This method returning {@code false} does not clearly indicate
	 * independent instances. It indicates non-singleton instances, which may correspond
	 * to a scoped bean as well. Use the {@link #isPrototype} operation to explicitly
	 * check for independent instances.
	 *  注意：该方法返回{@code false}并不表示该bean实例是一个独立的实例（每次都会创建），
	 *  也可能是限定作用域内的bean。使用{@link #isPrototype}方法去检测是否是独立的实例。
	 * <p>Translates aliases back to the corresponding canonical bean name.
	 *  如果给定的名称是别名，它将被翻译成相应的规范bean名称。
	 * <p>Will ask the parent factory if the bean cannot be found in this factory instance.
	 *  如果在本工厂实例中查找无果将询问父工厂。
	 * @param name the name of the bean to query
	 * @return whether this bean corresponds to a singleton instance
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @see #getBean
	 * @see #isPrototype
	 */
	boolean isSingleton(String name) throws NoSuchBeanDefinitionException;

	/**
	 * Is this bean a prototype? That is, will {@link #getBean} always return
	 * independent instances?
	 *  是否是原型模式的bean?{@link #getBean}方法是否一直返回一个独立的实例？
	 * <p>Note: This method returning {@code false} does not clearly indicate
	 * a singleton object. It indicates non-independent instances, which may correspond
	 * to a scoped bean as well. Use the {@link #isSingleton} operation to explicitly
	 * check for a shared singleton instance.
	 * 注意：该方法返回{@code false}并不表示该bean实例是一个单例实例，
	 * 也可能是限定作用域内的bean。使用{@link #isSingleton}方法去检测是否单例实例。
	 * <p>Translates aliases back to the corresponding canonical bean name.
	 *  如果给定的名称是别名，它将被翻译成相应的规范bean名称。
	 * <p>Will ask the parent factory if the bean cannot be found in this factory instance.
	 *  如果在本工厂实例中查找无果将询问父工厂。
	 * @param name the name of the bean to query
	 * @return whether this bean will always deliver independent instances
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @since 2.0.3
	 * @see #getBean
	 * @see #isSingleton
	 */
	boolean isPrototype(String name) throws NoSuchBeanDefinitionException;

	/**
	 * Check whether the bean with the given name matches the specified type.
	 * More specifically, check whether a {@link #getBean} call for the given name
	 * would return an object that is assignable to the specified target type.
	 * 检查限定名称的bean是否符合指定的类型。具体的讲，就是检查{@link #getBean}
	 * 使用name参数返回的bean是否符合指定的目标类型。
	 * <p>Translates aliases back to the corresponding canonical bean name.
	 *  如果给定的名称是别名，它将被翻译成相应的规范bean名称。
	 * <p>Will ask the parent factory if the bean cannot be found in this factory instance.
	 *  如果在本工厂实例中查找无果将询问父工厂。
	 * @param name the name of the bean to query
	 * @param typeToMatch the type to match against (as a {@code ResolvableType})
	 * @return {@code true} if the bean type matches,
	 * {@code false} if it doesn't match or cannot be determined yet
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @since 4.2
	 * @see #getBean
	 * @see #getType
	 */
	boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException;

	/**
	 * Check whether the bean with the given name matches the specified type.
	 * More specifically, check whether a {@link #getBean} call for the given name
	 * would return an object that is assignable to the specified target type.
	 * 检查限定名称的bean是否符合指定的类型。具体的讲，就是检查{@link #getBean}
	 * 使用name参数返回的bean是否符合指定的目标类型。
	 * <p>Translates aliases back to the corresponding canonical bean name.
	 *  如果给定的名称是别名，它将被翻译成相应的规范bean名称。
	 * <p>Will ask the parent factory if the bean cannot be found in this factory instance.
	 *  如果在本工厂实例中查找无果将询问父工厂。
	 * @param name the name of the bean to query
	 * @param typeToMatch the type to match against (as a {@code Class})
	 * @return {@code true} if the bean type matches,
	 * {@code false} if it doesn't match or cannot be determined yet
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @since 2.0.1
	 * @see #getBean
	 * @see #getType
	 */
	boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException;

	/**
	 * Determine the type of the bean with the given name. More specifically,
	 * determine the type of object that {@link #getBean} would return for the given name.
	 * 明确限定名称的bean的类型。具体讲，就是明确{@link #getBean}使用name参数返回的bean的类型。
	 * <p>For a {@link FactoryBean}, return the type of object that the FactoryBean creates,
	 * as exposed by {@link FactoryBean#getObjectType()}. This may lead to the initialization
	 * of a previously uninitialized {@code FactoryBean} (see {@link #getType(String, boolean)}).
	 * 对于{@link FactoryBean}，返回FactoryBean创建的对象类型，如{@link FactoryBean#getObjectType()}所示。
	 * 这可能导致初始化以前未初始化的{@code FactoryBean}(参见{@link #getType(String, boolean)})。
	 * <p>Translates aliases back to the corresponding canonical bean name.
	 *  如果给定的名称是别名，它将被翻译成相应的规范bean名称。
	 * <p>Will ask the parent factory if the bean cannot be found in this factory instance.
	 *  如果在本工厂实例中查找无果将询问父工厂。
	 * @param name the name of the bean to query
	 * @return the type of the bean, or {@code null} if not determinable
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @since 1.1.2
	 * @see #getBean
	 * @see #isTypeMatch
	 */
	@Nullable
	Class<?> getType(String name) throws NoSuchBeanDefinitionException;

	/**
	 * Determine the type of the bean with the given name. More specifically,
	 * determine the type of object that {@link #getBean} would return for the given name.
	 * 明确限定名称的bean的类型。具体讲，就是明确{@link #getBean}使用name参数返回的bean的类型。
	 * <p>For a {@link FactoryBean}, return the type of object that the FactoryBean creates,
	 * as exposed by {@link FactoryBean#getObjectType()}. Depending on the
	 * {@code allowFactoryBeanInit} flag, this may lead to the initialization of a previously
	 * uninitialized {@code FactoryBean} if no early type information is available.
	 * 对于{@link FactoryBean}，返回FactoryBean创建的对象类型，
	 * 如{@link FactoryBean#getObjectType()}所示。根据{@code allowFactoryBeanInit}标志，
	 * 如果没有可用的早期类型信息，这可能会导致初始化先前未初始化的{@code FactoryBean}。
	 * <p>Translates aliases back to the corresponding canonical bean name.
	 *  如果给定的名称是别名，它将被翻译成相应的规范bean名称。
	 * <p>Will ask the parent factory if the bean cannot be found in this factory instance.
	 *  如果在本工厂实例中查找无果将询问父工厂。
	 * @param name the name of the bean to query
	 * @param allowFactoryBeanInit whether a {@code FactoryBean} may get initialized
	 * just for the purpose of determining its object type
	 * @return the type of the bean, or {@code null} if not determinable
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @since 5.2
	 * @see #getBean
	 * @see #isTypeMatch
	 */
	@Nullable
	Class<?> getType(String name, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException;

	/**
	 * Return the aliases for the given bean name, if any.
	 *  返回bean的别名。
	 * <p>All of those aliases point to the same bean when used in a {@link #getBean} call.
	 *  使用{@link #getBean}调用时，所有的别名都返回相同的bean。
	 * <p>If the given name is an alias, the corresponding original bean name
	 * and other aliases (if any) will be returned, with the original bean name
	 * being the first element in the array.
	 *  如果使用别名调用该方法，相应的原始bean名称和其他别名都会返回，且原始bean名称在在数组的第一位。
	 * <p>Will ask the parent factory if the bean cannot be found in this factory instance.
	 *  如果在本工厂实例中查找无果将询问父工厂。
	 * @param name the bean name to check for aliases
	 * @return the aliases, or an empty array if none
	 * @see #getBean
	 */
	String[] getAliases(String name);

}
