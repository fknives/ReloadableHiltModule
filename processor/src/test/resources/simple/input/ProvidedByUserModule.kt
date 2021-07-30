package simple.input

import org.jetbrains.annotations.NotNull

class ProvidedByUserModule @UserModuleInject constructor(@NotNull fooDependency: FooDependency)