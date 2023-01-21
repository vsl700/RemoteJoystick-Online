using System;
using System.Reflection;

namespace RemoteJoystick_Online_ASP.Net.Areas.HelpPage.ModelDescriptions
{
    public interface IModelDocumentationProvider
    {
        string GetDocumentation(MemberInfo member);

        string GetDocumentation(Type type);
    }
}