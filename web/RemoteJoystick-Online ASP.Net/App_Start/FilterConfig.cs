using System.Web;
using System.Web.Mvc;

namespace RemoteJoystick_Online_ASP.Net
{
    public class FilterConfig
    {
        public static void RegisterGlobalFilters(GlobalFilterCollection filters)
        {
            filters.Add(new HandleErrorAttribute());
        }
    }
}
