using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace RemoteJoystick_Online_ASP.Net.Interface
{
    public interface IUserConnectionManager
    {
        void KeepUserConnection(string connectionCode, string connectionId);
        void RemoveUserConnection(string connectionId);
        List<string> GetUserConnections(string connectionCode);
    }
}
