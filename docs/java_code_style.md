# Java code style

We use [Google Style](https://google.github.io/styleguide/javaguide.html) conventions with 4
spaces (instead of 2) for indentation. Other extensions and changes are described below.

**Table of contents**

<!-- TOC -->
* [Javadocs](#javadocs)
* [Using `var` keyword](#using-var-keyword)
* [Dealing with `null`s](#dealing-with-nulls)
    * [`@ParametersAreNonnullByDefault`](#parametersarenonnullbydefault)
    * [Use `@Nullable` when required](#use-nullable-when-required)
    * [Using `checkNotNull()`](#using-checknotnull)
    * [Returning `null`](#returning-null)
* [Returning collections](#returning-collections)
* [Suppressing `unchecked` warnings using annotation](#suppressing-unchecked-warnings-using-annotation)
    * [Always comment on why a specific suppression is valid](#always-comment-on-why-a-specific-suppression-is-valid)
<!-- TOC -->

## Javadocs

See [Javadoc](javadoc.md) page for details.

## Using `var` keyword

Use the `var` keyword everywhere it's possible. It makes the code more readable and concise.

## Dealing with `null`s

### `@ParametersAreNonnullByDefault`

This annotation must be set in `package-info.java` of all the packages.

### Use `@Nullable` when required

Everything which is not annotated with `@Nullable` is not `null` by default.

### Using `checkNotNull()`

Non-nullity of parameters should be checked in `public` and `protected` methods to catch incorrect use of the API as early as possible.

If a method accepts two or more parameters that cannot be `null`, we call `Preconditions.checkNotNull()` in the order of parameters. `checkNotNull()` must be statically imported:

```java
import static com.google.common.base.Preconditions.checkNotNull;

protected void dispatch(Message message, CommandContext context) {
    checkNotNull(message);
    checkNotNull(context);
    ...
}
```

### Returning `null`

If a method returns a single object, it should never return `null`. Use `Optional` if the method can return nothing.

## Returning collections

If a method returns a collection, it should never return `null`. Instead, it should return an empty immutable collection.

## Suppressing `unchecked` warnings using annotation

Suppress unchecked warnings via standardized `@SuppressWarnings` annotation only instead of IntelliJ IDEA-specific `//noinspection` suppression style. We want to keep code compliant with any tool, not just with IntelliJ IDEA.

### Always comment on why a specific suppression is valid

Please do. Framework users and developers need to know the reason why a warning was suppressed.

