namespace GameServer.Controllers.MessageHandlers
{
    public interface IMessageHandler
    {
        byte[] Handle(byte[] message);
    }
}
