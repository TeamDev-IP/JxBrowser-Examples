# Javadoc

We follow Oracle's [Javadoc](https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html) conventions with some exceptions and additional rules.

**Table of contents**

<!-- TOC -->
* [Documenting non-private elements](#documenting-non-private-elements)
* [Layout for class description](#layout-for-class-description)
* [Using Javadoc tool](#using-javadoc-tool)
* [Avoiding new imports using Javadocs](#avoiding-new-imports-using-javadocs)
* [Make proper choice between `@link` and `@linkplain`](#make-proper-choice-between-link-and-linkplain)
* [Do not use `{@inheritDoc}` in a constructor documentation](#do-not-use-inheritdoc-in-a-constructor-documentation)
* [Use `@code` for Java keywords](#use-code-for-java-keywords)
<!-- TOC -->

## Documenting non-private elements

Document **all** non-private elements.

## Layout for class description

Use the following layout for a class description:

```java
/**
 * Class description goes here.
 *
 * <p>More details on the class.
 */
```

## Using Javadoc tool

We recommend using Javadoc tool periodically. Warnings produced during documentation generation may cause a mistakes in Javadocs. It's a sign to check the documentation correctness.

Analyze a Javadoc warnings, if any. It can improve your understanding of the Javadoc syntax or indicate outdated links or something else. This is why it is important to pay attention to these warnings.

## Avoiding new imports using Javadocs

Javadocs should not introduce new imports as it pollutes implicit class dependencies. Keep imports clean using fully qualified names. For example, if you link `Foo` class in a Javadocs, which does not appear explicitly in the code, don't write a Javadocs in the following manner:

```java
package com.example.foo;

import com.example.bar.Bar;

/**
 * @see Bar
 */
class Foo {
}
```

Instead, use the fully qualified name:

```java
package com.example.foo;

/**
 * @see com.example.bar.Bar
 */
class Foo {
}
```

## Make proper choice between `@link` and `@linkplain`

If a link label is a program element name, use `@link`:

```java
/**
 * Uses {@link com.example.Foo Foo} class.
 */
```

In this case, `Foo` will be displayed as a code.

Otherwise, if a link label is a plain text, use `@linkplain`:

```java
/**
 * Collects {@linkplain com.example.Document documents}.
 */
```

In this case, `documents` will be displayed as a plain text.

## Do not use `{@inheritDoc}` in a constructor documentation

It's an invalid Javadoc construction which produces an empty constructor documentation.

In this case `Derived` constructor documentation will be empty:

```java
class Base {
    /** 
     * Constructors of derived classes should
     * have package access level...
     */
    Base() {}
}

class Derived extends Base {
    /** {@inheritDoc} */
    Derived() {}
}
```

If you want to attract attention to the `Base` constructor doc, do it in this or similar ways:

```java
class Base {
    /** 
     * Constructors of derived classes should
     * have package access level...
     */
    Base() {}
}

class Derived extends Base {
    /** @see Base#Base() */
    Derived() {}
}
```

## Use `@code` for Java keywords

Wrap Java keywords, primitives, or literals like `null`, `true`, `false` with `@code`:

```java
/** Returns {@code true} if some condition... */
```
