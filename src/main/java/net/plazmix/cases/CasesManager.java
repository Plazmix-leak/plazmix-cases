package net.plazmix.cases;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Getter;
import lombok.NonNull;
import net.plazmix.cases.type.BaseCaseAnimation;
import net.plazmix.cases.type.BaseCaseBoxType;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

@Getter
public final class CasesManager {

    public static final CasesManager INSTANCE = new CasesManager();


    private final Collection<BaseCaseBoxType> registeredCaseTypes = new LinkedList<>();
    private final TIntObjectMap<Supplier<BaseCaseAnimation>> registeredCaseAnimations = new TIntObjectHashMap<>();

    public void registerCaseType(@NonNull BaseCaseBoxType caseBoxType) {
        registeredCaseTypes.add(caseBoxType);
    }

    public void registerCaseAnimation(int id, @NonNull Supplier<BaseCaseAnimation> caseAnimation) {
        registeredCaseAnimations.put(id, caseAnimation);
    }

    public int getAnimationId(BaseCaseAnimation baseCaseAnimation) {
        AtomicInteger animationId = new AtomicInteger(-1);

        registeredCaseAnimations.forEachEntry((id, animationSupplier) -> {

            if (animationSupplier.get().getTitleName().equals(baseCaseAnimation.getTitleName())) {
                animationId.set(id);
                return false;
            }

            return true;
        });

        return animationId.get();
    }

}
