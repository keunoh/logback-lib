package core.net.server;

public interface ClientVisitor<T extends Client> {

    void visit(T client);
}
