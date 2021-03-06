def connParams = [:];
connParams.broker = conf["smarts.broker"];
connParams.username = conf["smarts.username"];
connParams.password = conf["smarts.password"];
connParams.brokerUsername = conf["smarts.brokerUsername"];
connParams.brokerPassword = conf["smarts.brokerPassword"];

def LOG_PREFIX ="[${action}]:";
logger.warn("${LOG_PREFIX} Will execute action for alertId ${alert.alertId}");

def alertFromOpsGenie = opsgenie.getAlert(["alertId": alert.alertId]);
def notificationName = alertFromOpsGenie.alias;
if(!notificationName)
{
    logger.warn("${LOG_PREFIX} notificationName does not exists in alert details for alert id : ${alert.alertId}");
    throw new Exception("notificationName does not exists in alert details")
}


def domainName = alertFromOpsGenie.details["DomainName"];
if(!domainName)
{
    logger.warn("${LOG_PREFIX} domainName does not exists in alert details for alert id : ${alert.alertId}");
    throw new Exception("domainName does not exists in alert details")
}

connParams.domain=domainName;
logger.warn("${LOG_PREFIX} Will execute action for alert ${notificationName} on Smarts");
SmartsDatasource.execute(connParams){ds->
    if(action == "Acknowledge")
    {
        ds.invokeNotificationOperation(notificationName, "acknowledge", alert.username, "Acknowledged via OpsGenie");
    }
    else if(action == "unacknowledge")
    {
        ds.invokeNotificationOperation(notificationName, "unacknowledge", alert.username, "Unacknowledged via OpsGenie");
    }
    else if(action == "TakeOwnership")
    {
        ds.invokeNotificationOperation(notificationName, "takeOwnership", alert.username, "TakeOwnership via OpsGenie");
    }
    else if(action == "release ownership")
    {
        ds.invokeNotificationOperation(notificationName, "releaseOwnership", alert.username, "ReleaseOwnership via OpsGenie");
    }
    else if(action == "AddNote")
    {
        ds.invokeNotificationOperation(notificationName, "addAuditEntry", alert.username, alert.note);
    }
    else
    {
        throw new Exception("Unknown action ${action}")
    }
}
logger.warn("${LOG_PREFIX} Executed action for alert ${notificationName} on Smarts");
