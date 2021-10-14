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

import java.lang.annotation.Annotation;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

/**
 * Extension of the {@link BeanFactory} interface to be implemented by bean factories
 * that can enumerate all their bean instances, rather than attempting bean lookup
 * by name one by one as requested by clients. BeanFactory implementations that
 * preload all their bean definitions (such as XML-based factories) may implement
 * this interface.
 * 扩展了{@link BeanFactory}接口，由可以枚举所有bean实例的bean工厂实现，
 * 而不是由像客户端请求的那样逐个尝试按名称进行bean查找的bean工厂实现。
 * 预加载所有bean定义(例如基于xml的工厂)的BeanFactory实现可以实现这个接口。
 *
 * <p>If this is a {@link HierarchicalBeanFactory}, the return values will <i>not</i>
 * take any BeanFactory hierarchy into account, but will relate only to the beans
 * defined in the current factory. Use the {@link BeanFactoryUtils} helper class
 * to consider beans in ancestor factories too.
 * 如果这是一个{@link HierarchicalBeanFactory}，返回值将不会考虑任何BeanFactory层次结构，
 * 而只与当前工厂中定义的bean相关。使用{@link BeanFactoryUtils} helper类也可以考虑祖先工厂中的bean。
 *
 * <p>The methods in this interface will just respect bean definitions of this factory.
 * They will ignore any singleton beans that have been registered by other means like
 * {@link org.springframework.beans.factory.config.ConfigurableBeanFactory}'s
 * {@code registerSingleton} method, with the exception of
 * {@code getBeanNamesForType} and {@code getBeansOfType} which will check
 * such manually registered singletons too. Of course, BeanFactory's {@code getBean}
 * does allow transparent access to such special beans as well. However, in typical
 * scenarios, all beans will be defined by external bean definitions anyway, so most
 * applications don't need to worry about this differentiation.
 * 这个接口中的方法将只考虑这个工厂的bean定义。它们将忽略任何通过其他方式注册的单例bean，
 * 如{@link org.springframework.beans.factory.config.ConfigurableBeanFactory}的{@code registerSingleton}方法，
 * 但{@code getBeanNamesForType} 和{@code getBeansOfType} 也会检查这些手动注册的单例bean。
 * 当然，BeanFactory的{@code getBean}也允许对这些特殊bean进行透明访问。
 * 然而，在典型场景中，所有bean都将由外部bean定义定义，因此大多数应用程序不需要担心这种差异。
 *
 * <p><b>NOTE:</b> With the exception of {@code getBeanDefinitionCount}
 * and {@code containsBeanDefinition}, the methods in this interface
 * are not designed for frequent invocation. Implementations may be slow.
 * 注意：除了{@code getBeanDefinitionCount}和{@code containsBeanDefinition}之外，
 * 该接口中的方法不是为频繁调用而设计的。实现可能会很慢。
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 16 April 2001
 * @see HierarchicalBeanFactory
 * @see BeanFactoryUtils
 */
public interface ListableBeanFactory extends BeanFactory {

	/**
	 * Check if this bean factory contains a bean definition with the given name.
	 * <p>Does not consider any hierarchy this factory may participate in,
	 * and ignores any singleton beans that have been registered by
	 * other means than bean definitions.
	 * 检查此bean工厂是否包含具有给定名称的bean定义。不考虑此工厂可能参与的任何层次结构，
	 * 并忽略通过bean定义以外的方式注册的任何单例bean。
	 * @param beanName the name of the bean to look for
	 * @return if this bean factory contains a bean definition with the given name
	 * @see #containsBean
	 */
	boolean containsBeanDefinition(String beanName);

	/**
	 * Return the number of beans defined in the factory.
	 * <p>Does not consider any hierarchy this factory may participate in,
	 * and ignores any singleton beans that have been registered by
	 * other means than bean definitions.
	 * 返回工厂中定义的bean的数量。不考虑此工厂可能参与的任何层次结构，
	 * 并忽略通过bean定义以外的方式注册的任何单例bean。
	 * @return the number of beans defined in the factory
	 */
	int getBeanDefinitionCount();

	/**
	 * Return the names of all beans defined in this factory.
	 * <p>Does not consider any hierarchy this factory may participate in,
	 * and ignores any singleton beans that have been registered by
	 * other means than bean definitions.
	 * 返回该工厂中定义的所有bean的名称。不考虑此工厂可能参与的任何层次结构，
	 * 并忽略通过bean定义以外的方式注册的任何单例bean。
	 * @return the names of all beans defined in this factory,
	 * or an empty array if none defined
	 */
	String[] getBeanDefinitionNames();

	/**
	 * Return a provider for the specified bean, allowing for lazy on-demand retrieval
	 * of instances, including availability and uniqueness options.
	 * 返回指定bean的提供程序，允许延迟按需检索实例，包括可用性和惟一性选项。
	 * @param requiredType type the bean must match; can be an interface or superclass
	 * 检索bean的类型，可以是接口或者超类                       
	 * @param allowEagerInit whether stream-based access may initialize <i>lazy-init
	 * singletons</i> and <i>objects created by FactoryBeans</i> (or by factory methods
	 * with a "factory-bean" reference) for the type check
	 * 基于流的访问是否可以初始化FactoryBeans为类型检查创建的惰性初始化单例对象和对象   
	 * @return a corresponding provider handle
	 * @since 5.3
	 * @see #getBeanProvider(ResolvableType, boolean)
	 * @see #getBeanProvider(Class)
	 * @see #getBeansOfType(Class, boolean, boolean)
	 * @see #getBeanNamesForType(Class, boolean, boolean)
	 */
	<T> ObjectProvider<T> getBeanProvider(Class<T> requiredType, boolean allowEagerInit);

	/**
	 * Return a provider for the specified bean, allowing for lazy on-demand retrieval
	 * of instances, including availability and uniqueness options.
	 * 返回指定bean的提供程序，允许延迟按需检索实例，包括可用性和惟一性选项。
	 * @param requiredType type the bean must match; can be a generic type declaration.
	 * Note that collection types are not supported here, in contrast to reflective
	 * injection points. For programmatically retrieving a list of beans matching a
	 * specific type, specify the actual bean type as an argument here and subsequently
	 * use {@link ObjectProvider#orderedStream()} or its lazy streaming/iteration options.
	 * 注意，与反射注入点不同，这里不支持集合类型。要以编程方式检索与特定类型匹配的bean列表，
	 * 请在此处指定实际bean类型作为参数，然后使用{@link ObjectProvider#orderedStream()}或其惰性流/迭代选项。   
	 * @param allowEagerInit whether stream-based access may initialize <i>lazy-init
	 * singletons</i> and <i>objects created by FactoryBeans</i> (or by factory methods
	 * with a "factory-bean" reference) for the type check
	 * 基于流的访问是否可以初始化FactoryBeans为类型检查创建的惰性初始化单例对象和对象                         
	 * @return a corresponding provider handle
	 * @since 5.3
	 * @see #getBeanProvider(ResolvableType)
	 * @see ObjectProvider#iterator()
	 * @see ObjectProvider#stream()
	 * @see ObjectProvider#orderedStream()
	 * @see #getBeanNamesForType(ResolvableType, boolean, boolean)
	 */
	<T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType, boolean allowEagerInit);

	/**
	 * Return the names of beans matching the given type (including subclasses),
	 * judging from either bean definitions or the value of {@code getObjectType}
	 * in the case of FactoryBeans.
	 * 返回与给定类型(包括子类)匹配的bean名称，
	 * 根据bean定义或FactoryBeans的{@code getObjectType}的值判断。
	 * <p><b>NOTE: This method introspects top-level beans only.</b> It does <i>not</i>
	 * check nested beans which might match the specified type as well.
	 * 注意：此方法只检查顶级bean。它不检查可能与指定类型匹配的嵌套bean。
	 * <p>Does consider objects created by FactoryBeans, which means that FactoryBeans
	 * will get initialized. If the object created by the FactoryBean doesn't match,
	 * the raw FactoryBean itself will be matched against the type.
	 * 考虑由FactoryBeans创建的对象，这意味着FactoryBeans将被初始化。
	 * 如果FactoryBean创建的对象不匹配，则原始FactoryBean本身将与类型进行匹配。
	 * <p>Does not consider any hierarchy this factory may participate in.
	 * Use BeanFactoryUtils' {@code beanNamesForTypeIncludingAncestors}
	 * to include beans in ancestor factories too.
	 * 不考虑该工厂可能参与的任何层级。使用BeanFactoryUtils的
	 * {@code beanNamesForTypeIncludingAncestors}也可以包含祖先工厂中的bean名称。
	 * <p>Note: Does <i>not</i> ignore singleton beans that have been registered
	 * by other means than bean definitions.
	 * 注意：不忽略通过bean定义以外的其他方式注册的单例bean。
	 * <p>This version of {@code getBeanNamesForType} matches all kinds of beans,
	 * be it singletons, prototypes, or FactoryBeans. In most implementations, the
	 * result will be the same as for {@code getBeanNamesForType(type, true, true)}.
	 * 这个版本的{@code getBeanNamesForType}匹配所有类型的bean，无论是单例bean、原型bean还是FactoryBeans。
	 * 在大多数实现中，结果将与{@code getBeanNamesForType(type, true, true)}相同。
	 * <p>Bean names returned by this method should always return bean names <i>in the
	 * order of definition</i> in the backend configuration, as far as possible.
	 * 此方法返回的Bean名应该总是尽可能按照后端配置中的定义顺序返回Bean名。
	 * @param type the generically typed class or interface to match
	 * @return the names of beans (or objects created by FactoryBeans) matching
	 * the given object type (including subclasses), or an empty array if none
	 * @since 4.2
	 * @see #isTypeMatch(String, ResolvableType)
	 * @see FactoryBean#getObjectType
	 * @see BeanFactoryUtils#beanNamesForTypeIncludingAncestors(ListableBeanFactory, ResolvableType)
	 */
	String[] getBeanNamesForType(ResolvableType type);

	/**
	 * Return the names of beans matching the given type (including subclasses),
	 * judging from either bean definitions or the value of {@code getObjectType}
	 * in the case of FactoryBeans.
	 * 返回与给定类型(包括子类)匹配的bean名称，根据bean定义或FactoryBeans的{@code getObjectType}的值判断
	 * <p><b>NOTE: This method introspects top-level beans only.</b> It does <i>not</i>
	 * check nested beans which might match the specified type as well.
	 * 注意：此方法只检查顶级bean。它不检查可能与指定类型匹配的嵌套bean。
	 * <p>Does consider objects created by FactoryBeans if the "allowEagerInit" flag is set,
	 * which means that FactoryBeans will get initialized. If the object created by the
	 * FactoryBean doesn't match, the raw FactoryBean itself will be matched against the
	 * type. If "allowEagerInit" is not set, only raw FactoryBeans will be checked
	 * (which doesn't require initialization of each FactoryBean).
	 * 如果设置了“allowEagerInit”标志，则考虑FactoryBeans创建的对象(这意味着将初始化FactoryBeans)。
	 * 如果FactoryBean创建的对象不匹配，则原始FactoryBean本身将与类型匹配。
	 * 如果没有设置“allowEagerInit”，那么将只检查原始的FactoryBean(它不需要对每个FactoryBean进行初始化)。
	 * <p>Does not consider any hierarchy this factory may participate in.
	 * Use BeanFactoryUtils' {@code beanNamesForTypeIncludingAncestors}
	 * to include beans in ancestor factories too.
	 * 不考虑该工厂可能参与的任何层级。使用BeanFactoryUtils的
	 * {@code beanNamesForTypeIncludingAncestors}也可以包含祖先工厂中的bean名称。
	 * <p>Note: Does <i>not</i> ignore singleton beans that have been registered
	 * by other means than bean definitions.
	 * 注意：不忽略通过bean定义以外的其他方式注册的单例bean。
	 * <p>Bean names returned by this method should always return bean names <i>in the
	 * order of definition</i> in the backend configuration, as far as possible.
	 * 此方法返回的Bean名应该总是尽可能按照后端配置中的定义顺序返回Bean名。
	 * @param type the generically typed class or interface to match
	 * @param includeNonSingletons whether to include prototype or scoped beans too
	 * or just singletons (also applies to FactoryBeans)
	 * 是否也包括原型bean或限定范围的bean，还是只包括单例bean(也适用于FactoryBeans) 
	 * @param allowEagerInit whether to initialize <i>lazy-init singletons</i> and
	 * <i>objects created by FactoryBeans</i> (or by factory methods with a
	 * "factory-bean" reference) for the type check. Note that FactoryBeans need to be
	 * eagerly initialized to determine their type: So be aware that passing in "true"
	 * for this flag will initialize FactoryBeans and "factory-bean" references.
	 * 是否初始化lazy-init 单例对象和由FactoryBeans(或使用“factory-bean”引用的工厂方法)创建的对象，
	 * 以进行类型检查。注意，需要主动初始化FactoryBeans以确定它们的类型:所以请注意，
	 * 为这个标志传入“true”将初始化FactoryBeans和“factory-bean”引用。   
	 * @return the names of beans (or objects created by FactoryBeans) matching
	 * the given object type (including subclasses), or an empty array if none
	 * @since 5.2
	 * @see FactoryBean#getObjectType
	 * @see BeanFactoryUtils#beanNamesForTypeIncludingAncestors(ListableBeanFactory, ResolvableType, boolean, boolean)
	 */
	String[] getBeanNamesForType(ResolvableType type, boolean includeNonSingletons, boolean allowEagerInit);

	/**
	 * Return the names of beans matching the given type (including subclasses),
	 * judging from either bean definitions or the value of {@code getObjectType}
	 * in the case of FactoryBeans.
	 * 返回与给定类型(包括子类)匹配的bean名称，根据bean定义或FactoryBeans的{@code getObjectType}的值判断。
	 * <p><b>NOTE: This method introspects top-level beans only.</b> It does <i>not</i>
	 * check nested beans which might match the specified type as well.
	 * 注意：此方法只检查顶级bean。它不检查可能与指定类型匹配的嵌套bean。
	 * <p>Does consider objects created by FactoryBeans, which means that FactoryBeans
	 * will get initialized. If the object created by the FactoryBean doesn't match,
	 * the raw FactoryBean itself will be matched against the type.
	 * 考虑由FactoryBeans创建的对象，这意味着FactoryBeans将被初始化。
	 * 如果FactoryBean创建的对象不匹配，则原始FactoryBean本身将与类型进行匹配。
	 * <p>Does not consider any hierarchy this factory may participate in.
	 * Use BeanFactoryUtils' {@code beanNamesForTypeIncludingAncestors}
	 * to include beans in ancestor factories too.
	 * 不考虑该工厂可能参与的任何层级。使用BeanFactoryUtils的
	 * {@code beanNamesForTypeIncludingAncestors}也可以包含祖先工厂中的bean名称。
	 * <p>Note: Does <i>not</i> ignore singleton beans that have been registered
	 * by other means than bean definitions.
	 * 注意：不忽略通过bean定义以外的其他方式注册的单例bean。
	 * <p>This version of {@code getBeanNamesForType} matches all kinds of beans,
	 * be it singletons, prototypes, or FactoryBeans. In most implementations, the
	 * result will be the same as for {@code getBeanNamesForType(type, true, true)}.
	 * 这个版本的{@code getBeanNamesForType}匹配所有类型的bean，无论是单例bean、原型bean还是FactoryBeans。
	 * 在大多数实现中，结果将与{@code getBeanNamesForType(type, true, true)}相同。
	 * <p>Bean names returned by this method should always return bean names <i>in the
	 * order of definition</i> in the backend configuration, as far as possible.
	 * 此方法返回的Bean名应该总是尽可能按照后端配置中的定义顺序返回Bean名。
	 * @param type the class or interface to match, or {@code null} for all bean names
	 * @return the names of beans (or objects created by FactoryBeans) matching
	 * the given object type (including subclasses), or an empty array if none
	 * @see FactoryBean#getObjectType
	 * @see BeanFactoryUtils#beanNamesForTypeIncludingAncestors(ListableBeanFactory, Class)
	 */
	String[] getBeanNamesForType(@Nullable Class<?> type);

	/**
	 * Return the names of beans matching the given type (including subclasses),
	 * judging from either bean definitions or the value of {@code getObjectType}
	 * in the case of FactoryBeans.
	 * 返回与给定类型(包括子类)匹配的bean名称，根据bean定义或FactoryBeans的{@code getObjectType}的值判断。
	 * <p><b>NOTE: This method introspects top-level beans only.</b> It does <i>not</i>
	 * check nested beans which might match the specified type as well.
	 * 注意：此方法只检查顶级bean。它不检查可能与指定类型匹配的嵌套bean。
	 * <p>Does consider objects created by FactoryBeans if the "allowEagerInit" flag is set,
	 * which means that FactoryBeans will get initialized. If the object created by the
	 * FactoryBean doesn't match, the raw FactoryBean itself will be matched against the
	 * type. If "allowEagerInit" is not set, only raw FactoryBeans will be checked
	 * (which doesn't require initialization of each FactoryBean).
	 * 如果设置了“allowEagerInit”标志，则考虑FactoryBeans创建的对象(这意味着将初始化FactoryBeans)。
	 * 如果FactoryBean创建的对象不匹配，则原始FactoryBean本身将与类型匹配。
	 * 如果没有设置“allowEagerInit”，那么将只检查原始的FactoryBean(它不需要对每个FactoryBean进行初始化)。
	 * <p>Does not consider any hierarchy this factory may participate in.
	 * Use BeanFactoryUtils' {@code beanNamesForTypeIncludingAncestors}
	 * to include beans in ancestor factories too.
	 * 不考虑该工厂可能参与的任何层级。使用BeanFactoryUtils的
	 * {@code beanNamesForTypeIncludingAncestors}也可以包含祖先工厂中的bean名称。
	 * <p>Note: Does <i>not</i> ignore singleton beans that have been registered
	 * by other means than bean definitions.
	 * 注意：不忽略通过bean定义以外的其他方式注册的单例bean。
	 * <p>Bean names returned by this method should always return bean names <i>in the
	 * order of definition</i> in the backend configuration, as far as possible.
	 * 此方法返回的Bean名应该总是尽可能按照后端配置中的定义顺序返回Bean名。
	 * @param type the class or interface to match, or {@code null} for all bean names
	 * @param includeNonSingletons whether to include prototype or scoped beans too
	 * or just singletons (also applies to FactoryBeans)
	 * 是否也包括原型bean或限定范围的bean，还是只包括单例bean(也适用于FactoryBeans)                                
	 * @param allowEagerInit whether to initialize <i>lazy-init singletons</i> and
	 * <i>objects created by FactoryBeans</i> (or by factory methods with a
	 * "factory-bean" reference) for the type check. Note that FactoryBeans need to be
	 * eagerly initialized to determine their type: So be aware that passing in "true"
	 * for this flag will initialize FactoryBeans and "factory-bean" references.
	 * 是否初始化lazy-init 单例对象和由FactoryBeans(或使用“factory-bean”引用的工厂方法)创建的对象，
	 * 以进行类型检查。注意，需要主动初始化FactoryBeans以确定它们的类型:所以请注意，
	 * 为这个标志传入“true”将初始化FactoryBeans和“factory-bean”引用。                         
	 * @return the names of beans (or objects created by FactoryBeans) matching
	 * the given object type (including subclasses), or an empty array if none
	 * @see FactoryBean#getObjectType
	 * @see BeanFactoryUtils#beanNamesForTypeIncludingAncestors(ListableBeanFactory, Class, boolean, boolean)
	 */
	String[] getBeanNamesForType(@Nullable Class<?> type, boolean includeNonSingletons, boolean allowEagerInit);

	/**
	 * Return the bean instances that match the given object type (including
	 * subclasses), judging from either bean definitions or the value of
	 * {@code getObjectType} in the case of FactoryBeans.
	 * 返回与给定对象类型(包括子类)匹配的bean实例，从bean定义或FactoryBeans的{@code getObjectType}的值判断。
	 * <p><b>NOTE: This method introspects top-level beans only.</b> It does <i>not</i>
	 * check nested beans which might match the specified type as well.
	 * 注意：此方法只检查顶级bean。它不检查可能与指定类型匹配的嵌套bean。
	 * <p>Does consider objects created by FactoryBeans, which means that FactoryBeans
	 * will get initialized. If the object created by the FactoryBean doesn't match,
	 * the raw FactoryBean itself will be matched against the type.
	 * 如果设置了“allowEagerInit”标志，则考虑FactoryBeans创建的对象(这意味着将初始化FactoryBeans)。
	 * 如果FactoryBean创建的对象不匹配，则原始FactoryBean本身将与类型匹配。
	 * <p>Does not consider any hierarchy this factory may participate in.
	 * Use BeanFactoryUtils' {@code beansOfTypeIncludingAncestors}
	 * to include beans in ancestor factories too.
	 * 不考虑该工厂可能参与的任何层级。使用BeanFactoryUtils的
	 * {@code beanNamesForTypeIncludingAncestors}也可以包含祖先工厂中的bean名称。
	 * <p>Note: Does <i>not</i> ignore singleton beans that have been registered
	 * by other means than bean definitions.
	 * 注意：不忽略通过bean定义以外的其他方式注册的单例bean。
	 * <p>This version of getBeansOfType matches all kinds of beans, be it
	 * singletons, prototypes, or FactoryBeans. In most implementations, the
	 * result will be the same as for {@code getBeansOfType(type, true, true)}.
	 * 这个版本的getBeansOfType匹配所有类型的bean，无论是单例bean、原型bean还是FactoryBeans。
	 * 在大多数实现中，结果将与{@code getBeansOfType(type, true, true)}相同。
	 * <p>The Map returned by this method should always return bean names and
	 * corresponding bean instances <i>in the order of definition</i> in the
	 * backend configuration, as far as possible.
	 * 此方法返回的Map应该总是尽可能按照后端配置中定义的顺序返回bean名称和相应的bean实例。
	 * @param type the class or interface to match, or {@code null} for all concrete beans
	 * @return a Map with the matching beans, containing the bean names as
	 * keys and the corresponding bean instances as values
	 * @throws BeansException if a bean could not be created
	 * @since 1.1.2
	 * @see FactoryBean#getObjectType
	 * @see BeanFactoryUtils#beansOfTypeIncludingAncestors(ListableBeanFactory, Class)
	 */
	<T> Map<String, T> getBeansOfType(@Nullable Class<T> type) throws BeansException;

	/**
	 * Return the bean instances that match the given object type (including
	 * subclasses), judging from either bean definitions or the value of
	 * {@code getObjectType} in the case of FactoryBeans.
	 * 返回与给定类型(包括子类)匹配的bean名称，根据bean定义或FactoryBeans的{@code getObjectType}的值判断。
	 * <p><b>NOTE: This method introspects top-level beans only.</b> It does <i>not</i>
	 * check nested beans which might match the specified type as well.
	 * 注意：此方法只检查顶级bean。它不检查可能与指定类型匹配的嵌套bean。
	 * <p>Does consider objects created by FactoryBeans if the "allowEagerInit" flag is set,
	 * which means that FactoryBeans will get initialized. If the object created by the
	 * FactoryBean doesn't match, the raw FactoryBean itself will be matched against the
	 * type. If "allowEagerInit" is not set, only raw FactoryBeans will be checked
	 * (which doesn't require initialization of each FactoryBean).
	 * 如果设置了“allowEagerInit”标志，则考虑FactoryBeans创建的对象(这意味着将初始化FactoryBeans)。
	 * 如果FactoryBean创建的对象不匹配，则原始FactoryBean本身将与类型匹配。
	 * 如果没有设置“allowEagerInit”，那么将只检查原始的FactoryBean(它不需要对每个FactoryBean进行初始化)。
	 * <p>Does not consider any hierarchy this factory may participate in.
	 * Use BeanFactoryUtils' {@code beansOfTypeIncludingAncestors}
	 * to include beans in ancestor factories too.
	 * 不考虑该工厂可能参与的任何层级。使用BeanFactoryUtils的
	 * {@code beanNamesForTypeIncludingAncestors}也可以包含祖先工厂中的bean名称。
	 * <p>Note: Does <i>not</i> ignore singleton beans that have been registered
	 * by other means than bean definitions.
	 * 注意：不忽略通过bean定义以外的其他方式注册的单例bean。
	 * <p>The Map returned by this method should always return bean names and
	 * corresponding bean instances <i>in the order of definition</i> in the
	 * backend configuration, as far as possible.
	 * 此方法返回的Bean名应该总是尽可能按照后端配置中的定义顺序返回Bean名。
	 * @param type the class or interface to match, or {@code null} for all concrete beans
	 * @param includeNonSingletons whether to include prototype or scoped beans too
	 * or just singletons (also applies to FactoryBeans)
	 * 是否也包括原型bean或限定范围的bean，还是只包括单例bean(也适用于FactoryBeans)                               
	 * @param allowEagerInit whether to initialize <i>lazy-init singletons</i> and
	 * <i>objects created by FactoryBeans</i> (or by factory methods with a
	 * "factory-bean" reference) for the type check. Note that FactoryBeans need to be
	 * eagerly initialized to determine their type: So be aware that passing in "true"
	 * for this flag will initialize FactoryBeans and "factory-bean" references.
	 * 是否初始化lazy-init 单例对象和由FactoryBeans(或使用“factory-bean”引用的工厂方法)创建的对象，
	 * 以进行类型检查。注意，需要主动初始化FactoryBeans以确定它们的类型:所以请注意，
	 * 为这个标志传入“true”将初始化FactoryBeans和“factory-bean”引用。                         
	 * @return a Map with the matching beans, containing the bean names as
	 * keys and the corresponding bean instances as values
	 * @throws BeansException if a bean could not be created
	 * @see FactoryBean#getObjectType
	 * @see BeanFactoryUtils#beansOfTypeIncludingAncestors(ListableBeanFactory, Class, boolean, boolean)
	 */
	<T> Map<String, T> getBeansOfType(@Nullable Class<T> type, boolean includeNonSingletons, boolean allowEagerInit)
			throws BeansException;

	/**
	 * Find all names of beans which are annotated with the supplied {@link Annotation}
	 * type, without creating corresponding bean instances yet.
	 * 找到所有使用{@link Annotation}类型注释的bean的名称，但还没有创建相应的bean实例。
	 * <p>Note that this method considers objects created by FactoryBeans, which means
	 * that FactoryBeans will get initialized in order to determine their object type.
	 * 注意，此方法考虑由FactoryBeans创建的对象，这意味着FactoryBeans将被初始化，以确定它们的对象类型。
	 * @param annotationType the type of annotation to look for
	 * (at class, interface or factory method level of the specified bean)
	 * 要查找的注释类型(在指定bean的类、接口或工厂方法级别)                    
	 * @return the names of all matching beans
	 * @since 4.0
	 * @see #findAnnotationOnBean
	 */
	String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType);

	/**
	 * Find all beans which are annotated with the supplied {@link Annotation} type,
	 * returning a Map of bean names with corresponding bean instances.
	 * 到所有使用{@link Annotation}类型注释的bean，返回一个带有相应bean实例的bean名称的Map。
	 * <p>Note that this method considers objects created by FactoryBeans, which means
	 * that FactoryBeans will get initialized in order to determine their object type.
	 * 注意，此方法考虑由FactoryBeans创建的对象，这意味着FactoryBeans将被初始化，以确定它们的对象类型。
	 * @param annotationType the type of annotation to look for
	 * (at class, interface or factory method level of the specified bean)
	 * 要查找的注释类型(在指定bean的类、接口或工厂方法级别)                          
	 * @return a Map with the matching beans, containing the bean names as
	 * keys and the corresponding bean instances as values
	 * @throws BeansException if a bean could not be created
	 * @since 3.0
	 * @see #findAnnotationOnBean
	 */
	Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) throws BeansException;

	/**
	 * Find an {@link Annotation} of {@code annotationType} on the specified bean,
	 * traversing its interfaces and super classes if no annotation can be found on
	 * the given class itself, as well as checking the bean's factory method (if any).
	 * 在指定的bean上找到{@code annotationType}的{@link Annotation}，
	 * 如果在给定的类上找不到注释，就遍历它的接口和超类，并检查bean的工厂方法(如果有的话)。
	 * @param beanName the name of the bean to look for annotations on
	 * @param annotationType the type of annotation to look for
	 * (at class, interface or factory method level of the specified bean)
	 * 要查找的注释类型(在指定bean的类、接口或工厂方法级别)                         
	 * @return the annotation of the given type if found, or {@code null} otherwise
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @since 3.0
	 * @see #getBeanNamesForAnnotation
	 * @see #getBeansWithAnnotation
	 */
	@Nullable
	<A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType)
			throws NoSuchBeanDefinitionException;

}
