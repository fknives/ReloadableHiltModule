package simple.input;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import java.lang.Override;
import javax.inject.Provider;
import org.jetbrains.annotations.NotNull;

@Module
@InstallIn(SingletonComponent.class)
public class ReloadUserModuleInjectModuleImpl implements ReloadUserModuleInjectModule {
    private ProvidedByUserModule cachedProvidedByUserModule = null;

    @Provides
    public ReloadUserModuleInjectModule provideReloadUserModuleInjectModule() {
        return this;
    }

    @Provides
    public ProvidedByUserModule provideProvidedByUserModule(
            @NotNull Provider<FooDependency> fooDependency) {
        if (cachedProvidedByUserModule == null) {
            cachedProvidedByUserModule = new ProvidedByUserModule (fooDependency.get());
        }
        return cachedProvidedByUserModule;
    }

    @Override
    public void reload() {
        cachedProvidedByUserModule = null;
    }
}
