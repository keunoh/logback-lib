package core.joran.spi;

public record HostClassAndPropertyDouble(Class<?> hostClass, String propertyName) {

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final HostClassAndPropertyDouble other = (HostClassAndPropertyDouble) obj;
        if (hostClass == null) {
            if (other.hostClass != null)
                return false;
        } else if (!hostClass.equals(other.hostClass))
            return false;
        if (propertyName == null) {
            if (other.propertyName != null)
                return false;
        } else if (!propertyName.equals(other.propertyName)) {
            return false;
        }
        return true;
    }
}