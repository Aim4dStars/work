:: http://www.rallydev.com/help/rally-integrations-faq-and-best-practices#Running-as-scheduled-task
:: Set the "Connector folder" (where the connector has been installed).
set cfolder="D:\RallyConnectorforQualityCenter"

:: Set the "Working folder" (where the configurations files are kept and
:: modified; this is typically somewhere in the user's home folder).
set wfolder="D:\RallyConnectorforQualityCenter"

:: Invoke the connector.
:: %cfolder%\rally2_qc_connector.exe %wfolder%\syd_rally_analyse.xml %wfolder%\syd_rally_fix.xml %wfolder%\syd_qc_sync.xml -1 >> %wfolder%\Connector.log 2>&1
%cfolder%\rally2_qc_connector.exe %wfolder%\syd_rally_analyse.xml -1 >> %wfolder%\Connector.log 2>&1

::the end::