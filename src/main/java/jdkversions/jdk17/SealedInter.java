package jdkversions.jdk17;

public sealed interface SealedInter permits Sub1, Sub2, Sub3 {
}

non-sealed interface Sub1 extends SealedInter {

}

final class Sub2 implements SealedInter {

}

non-sealed class Sub3 implements SealedInter {

}
