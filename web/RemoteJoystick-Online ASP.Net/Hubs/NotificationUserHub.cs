using Microsoft.AspNet.SignalR;
using RemoteJoystick_Online_ASP.Net.Interface;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.Web;

namespace RemoteJoystick_Online_ASP.Net.Hubs
{
    public class NotificationUserHub : Hub
    {
        private readonly IUserConnectionManager _userConnectionManager;

        public NotificationUserHub(IUserConnectionManager userConnectionManager)
        {
            _userConnectionManager = userConnectionManager;
        }

        public override Task OnConnected()
        {
            var userId = Context.Request.QueryString["userId"];
            _userConnectionManager.KeepUserConnection(userId, Context.ConnectionId);

            return base.OnConnected();
        }

        public override Task OnDisconnected(bool stopCalled)
        {
            var connectionId = Context.ConnectionId;
            _userConnectionManager.RemoveUserConnection(connectionId);

            return base.OnDisconnected(stopCalled);
        }

        public override Task OnReconnected()
        {
            return OnConnected();
        }
    }
}