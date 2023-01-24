using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace RemoteJoystick_Online_ASP.Net.Interface
{
    public interface IUserConnectionManager
    {
        bool KeepUserConnection(string connectionCode, string connectionId);
        void RemoveUserConnection(string connectionId);
        string[] GetUserConnections(string connectionCode);
    }
}
