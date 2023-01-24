using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace RemoteJoystick_Online_ASP.Net.Interface
{
    public class UserConnectionManager : IUserConnectionManager
    {
        public class DesktopMobilePair {
            public string DesktopConnectionId { get; set; }
            public string MobileConnectionId { get; set; }
        }

        public static Dictionary<string, DesktopMobilePair> connections = new Dictionary<string, DesktopMobilePair>();

        public string[] GetUserConnections(string connectionCode)
        {
            lock (connections)
            {
                if (!connections.ContainsKey(connectionCode))
                    return null;

                var dmPair = connections[connectionCode];
                return new string[]{ dmPair.DesktopConnectionId, dmPair.MobileConnectionId };
            }
        }

        public bool KeepUserConnection(string connectionCode, string connectionId)
        {
            lock (connections)
            {
                bool isMobileBool = IsMobile(connectionCode);
                connectionCode = GetNormalCode(connectionCode);
                if (!connections.ContainsKey(connectionCode)) {
                    if (!isMobileBool)
                    {
                        DesktopMobilePair dmPair = new DesktopMobilePair { DesktopConnectionId = connectionId };
                        connections[connectionCode] = dmPair;

                        return true;
                    }
                }
                else if(isMobileBool)
                {
                    connections[connectionCode].MobileConnectionId = connectionId;
                    return true;
                }
            }

            return false;
        }

        public void RemoveUserConnection(string connectionId)
        {
            lock (connections)
            {
                foreach (var connectionCode in connections.Keys)
                {
                    bool isMobileBool = IsMobile(connectionCode);
                    string tempCode = GetNormalCode(connectionCode);
                    if (connections[tempCode].DesktopConnectionId == connectionId)
                    {
                        connections[tempCode].DesktopConnectionId = null;
                        break;
                    }
                    else if(connections[tempCode].MobileConnectionId == connectionId)
                    {
                        connections[tempCode].MobileConnectionId = null;
                        break;
                    }
                }
            }
        }

        private bool IsMobile(string connectionCode)
        {
            return connectionCode.Last() == 'm';
        }

        private string GetNormalCode(string connectionCode)
        {
            return connectionCode.Substring(0, connectionCode.Length - 1);
        }
    }
}
