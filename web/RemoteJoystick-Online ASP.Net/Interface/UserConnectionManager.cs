using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace RemoteJoystick_Online_ASP.Net.Interface
{
    public class UserConnectionManager : IUserConnectionManager
    {
        public static Dictionary<string, List<string>> connections = new Dictionary<string, List<string>();

        public List<string> GetUserConnections(string connectionCode)
        {
            lock (connections)
            {
                if (!connections.ContainsKey(connectionCode))
                    return null;

                return connections[connectionCode];
            }
        }

        public void KeepUserConnection(string connectionCode, string connectionId)
        {
            lock (connections)
            {
                if (!connections.ContainsKey(connectionCode))
                    connections[connectionCode] = new List<string>();

                if (!connections[connectionCode].Contains(connectionId))
                    connections[connectionCode].Add(connectionId);
            }
        }

        public void RemoveUserConnection(string connectionId)
        {
            lock (connections)
            {
                foreach(var connectionCode in connections.Keys)
                {
                    if (connections[connectionCode].Contains(connectionId))
                    {
                        connections[connectionCode].Remove(connectionId);
                        break;
                    }
                }
            }
        }
    }
}