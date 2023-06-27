package nbbrd.console.picocli;

import nbbrd.service.Quantifier;
import nbbrd.service.ServiceDefinition;
import nbbrd.service.ServiceId;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface Profile {

    String getId();

    default Profile apply(Profilable... list) {
        ProfileLoader.Spi.load()
                .stream()
                .filter(provider -> provider.getId().equals(getId()))
                .forEach(provider -> {
                    for (Profilable o : list) {
                        provider.apply(o);
                    }
                });
        return this;
    }

    @ServiceDefinition(
            quantifier = Quantifier.MULTIPLE
    )
    interface Spi {

        @ServiceId
        @NonNull
        String getId();

        void apply(@NonNull Profilable profilable);
    }
}
