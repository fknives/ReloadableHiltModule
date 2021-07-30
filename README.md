# ReloadableHiltModule
Annotation processor which can generates modules integrated with Hilt that can be reload.
Reload means all the objects provided by the Module will be kept until it's reloaded. At that point the provided instances are recreated.

## Why

Hilt is opinionated and clear on it's rules. However with dagger I was able to have Components scoped for a Logged In User, or for a specific flow.

Today the equivalent can be done as detailed here:
[https://medium.com/androiddevelopers/hilt-adding-components-to-the-hierarchy-96f207d6d92d](https://medium.com/androiddevelopers/hilt-adding-components-to-the-hierarchy-96f207d6d92d)

However this requires to inject a UserManager, and inject your dependencies from EntryPoints. Which doesn't seem to worth it for me.

Of course if you have Single Activity for your logged in state, Hilt works just fine.

### My case

In my case however, I still have multiple activities on some projects, so It would be great to easily clean caches when my user logs out.

I had the idea to borrow the concept [loadKoinModules](https://insert-koin.io/docs/reference/koin-core/start-koin#loading-modules-after-startkoin) with override which can be used just to do that.
So in this annotation processor I generate a module definition which works similarly.

## Setup

*Latest version:* ![Latest release](https://img.shields.io/github/v/release/fknives/ReloadableHiltModule)

// top level build.gradle
//..
```groovy
allprojects {
    repositories {
        // ...
        maven {
            url "https://maven.pkg.github.com/fknives/ReloadableHiltModule"
            credentials {
                username = project.findProperty("GITHUB_USERNAME") ?: System.getenv("GITHUB_USERNAME")
                password = project.findProperty("GITHUB_TOKEN") ?: System.getenv("GITHUB_TOKEN")
            }
            // how to get token
            // https://docs.github.com/en/github/authenticating-to-github/keeping-your-account-and-data-secure/creating-a-personal-access-token
        }
    }
}
```

// local build.gradle
```groovy
kapt {
    correctErrorTypes = true
}

dependencies {
    implementation "org.fnives.library.reloadable.module:annotation:$latest_version"
    kapt "org.fnives.library.reloadable.module:annotation-processor:$latest_version"
}
```

## Usage

Create your ModuleAnnotation, example:

```kotlin
import org.fnives.library.reloadable.module.annotation.ReloadableModule

@ReloadableModule
@Target(AnnotationTarget.CONSTRUCTOR)
@Retention(AnnotationRetention.SOURCE)
annotation class LoggedInModuleInject
```

Apply to your classes such as:

```kotlin
class ContentRepository @LoggedInModuleInject constructor(private val contentGenerator: ContentGenerator) {
```

Where you want to reload the module, inject the generated Helper class:

```kotlin
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val reloadLoggedInModuleInjectModule: ReloadLoggedInModuleInjectModule
) {

    fun invoke() {
        reloadLoggedInModuleInjectModule.reload()
    }
}
```