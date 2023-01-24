using Microsoft.AspNetCore.SignalR;
using RemoteJoystick_Online_ASP.Net.Interface;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace RemoteJoystick_Online_ASP.Net.Hubs
{
    public class JoystickHub : Hub
    {
        private readonly IUserConnectionManager _userConnectionManager;

        public JoystickHub(IUserConnectionManager userConnectionManager)
        {
            _userConnectionManager = userConnectionManager;
        }

        public override Task OnConnectedAsync()
        {
            var userId = Context.GetHttpContext().Request.Query["userId"];
            if(!_userConnectionManager.KeepUserConnection(userId, Context.ConnectionId))
            {
                Context.GetHttpContext().Response.StatusCode = 400;
            }
            

            return base.OnConnectedAsync();
        }
        
        public override Task OnDisconnectedAsync(Exception exception)
        {
            var connectionId = Context.ConnectionId;
            _userConnectionManager.RemoveUserConnection(connectionId);

            return base.OnDisconnectedAsync(exception);
        }
    }
}
