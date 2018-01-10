package kamon.agent.profiler;

import lombok.Value;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;


@Value
public class Method implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final Method ROOT = new Method(ManagementFactory.getRuntimeMXBean().getName(), "ROOT");

    private static final Map<String, Map<String, Method>> INSTANCE_REPO = new HashMap<>(1024);

    private final String declaringClass;

    private final String methodName;

    public Method(final StackTraceElement elem) {
        this(elem.getClassName(), elem.getMethodName());
    }

    public Method(final Class<?> clasz,  final String methodName) {
        this(clasz.getName(), methodName);
    }

    public Method(final String declaringClass,  final String methodName) {
        this.declaringClass = declaringClass;
        this.methodName = methodName;
    }


    public String getDeclaringClass() {
        return declaringClass;
    }


    public String getMethodName() {
        return methodName;
    }

    @Override
    public int hashCode() {
        return 47 * declaringClass.hashCode() + methodName.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Method other = (Method) obj;
        if (!this.declaringClass.equals(other.declaringClass)) {
            return false;
        }
        return this.methodName.equals(other.methodName);
    }

    @Override
    public String toString() {
        return methodName + '@' + declaringClass;
    }

    public void toWriter(final Writer w) throws IOException {
        w.append(methodName).append("@").append(declaringClass);
    }


    public static Method getMethod(final StackTraceElement elem) {
        return getMethod(elem.getClassName(), elem.getMethodName());
    }

    /*
     * this function is to allow reuse of Method instances.
     * not thread safe, use with care, see description for suppressed findbugs bug for more detail.
     */
    public static synchronized Method getMethod(final String className, final String methodName) {
        Map<String, Method> mtom = INSTANCE_REPO.get(className);
        Method result;
        if (mtom == null) {
            mtom = new HashMap<>(4);
            result = new Method(className, methodName);
            mtom.put(methodName, result);
            INSTANCE_REPO.put(className, mtom);
        } else {
            result = mtom.get(methodName);
            if (result == null) {
                result = new Method(className, methodName);
                mtom.put(methodName, result);
            }
        }
        return result;
    }

}
