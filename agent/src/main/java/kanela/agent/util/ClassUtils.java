package kanela.agent.util;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeList;

final public class ClassUtils {

    public static String methodSignature(MethodDescription methodDescription) {
        StringBuilder builder = new StringBuilder(methodDescription.getInternalName());
        builder.append('(');
        final TypeList typeDescriptions = methodDescription.getParameters().asTypeList().asErasures();
        if (!typeDescriptions.isEmpty()) {
            builder.append(typeDescriptions.get(0).getCanonicalName());
            for (int i = 1; i < typeDescriptions.size(); i++) {
                builder.append(',');
                builder.append(typeDescriptions.get(i).getCanonicalName());
            }
        }
        builder.append(')');
        return builder.toString();
    }

}
