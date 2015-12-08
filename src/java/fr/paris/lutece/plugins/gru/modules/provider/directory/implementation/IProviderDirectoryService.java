package fr.paris.lutece.plugins.gru.modules.provider.directory.implementation;
import fr.paris.lutece.plugins.directory.business.Directory;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.workflow.modules.notifygru.business.TaskNotifyGruConfig;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.util.ReferenceList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;


/**
 *
 *
 */
public interface IProviderDirectoryService
{
    // CHECKS

    /**
     * Check if the given entry type id is accepted for the email or the sms
     * @param nIdEntryType the id entry type
     * @return true if it is accepted, false otherwise
     */
    boolean isEntryTypeEmailSMSAccepted( int nIdEntryType );

    /**
     * Check if the given entry type id is accepted for the user guid
     * @param nIdEntryType the id entry type
     * @return true if it is accepted, false otherwise
     */
    boolean isEntryTypeUserGuidAccepted( int nIdEntryType );

    /**
     * Check if the given entry type id is accepted for file
     * @param nIdEntryType the id entry type
     * @return true if it is accepted, false otherwise
     */
    boolean isEntryTypeFileAccepted( int nIdEntryType );

    /**
     * Check if the entry is refused (values set in the
     * workflow-notifydirectory.properties)
     * @param nIdEntryType the id entry type
     * @return true if it is refused, false otherwise
     */
    boolean isEntryTypeRefused( int nIdEntryType );

    // GETS

    /**
     * Get the list of states
     * @param nIdAction  the id action
     * @return a ReferenceList
     */
    ReferenceList getListStates( int nIdAction );

    /**
     * Get the list of directorise
     * @return a ReferenceList
     */
    ReferenceList getListDirectories(  );

    /**
     * Get the mailing list
     * @param request the HTTP request
     * @return a ReferenceList
     */
    ReferenceList getMailingList( HttpServletRequest request );

   
    /**
     * Get the list of entries that have the accepted type (which are defined in
     * <b>workflow-notifydirectory.properties</b>)
     * @param nidDirectory
     * @param locale the Locale
     * @return a ReferenceList
     */
    ReferenceList getListEntriesUserGuid(  int nidDirectory, Locale locale );

    /**
     * Get the list of entries that have the accepted type (which are defined in
     * <b>workflow-notifydirectory.properties</b>)
     * @param nidDirectory
     * @param locale the Locale
     * @return a ReferenceList
     */
    ReferenceList getListEntriesEmailSMS( int nidDirectory, Locale locale );

    /**
     * Get the list of entries that have not the refused type (which are defined
     * in the <b>workflow-notifydirectory.properties</b>). <br />
     * This list will be displayed as a freemarker label that the webmaster can
     * use to write the notifications.
     * @param nidDirectory
     * @return a list of {@link IEntry}
     */
    List<IEntry> getListEntriesFreemarker(  int nidDirectory);
    /**
     */
    
    /**
     *
     * @param nidDirectory
     * @param locale
     * @return 
     */
     List<IEntry> getListEntriesFile(int nidDirectory, Locale locale); 



    /**
     * @param nidDirectory
     * @return */
     List<IEntry> getListEntries(int nidDirectory);

  
    /**
     * Get the email from either an entry containing the email, or an entry
     * containing the user guid
     * @param nPositionEmail
     * @param nIdRecord the id record
     * @param nIdDirectory the id directory
     * @return the email
     */
    String getEmail( int nPositionEmail, int nIdRecord, int nIdDirectory );
    
    /**
     * @param nPositionDemand
     * @param nIdRecord
     * @param nIdDirectory
     * @return */
     int getIdDemand( int nPositionDemand, int nIdRecord, int nIdDirectory );
    
     /**
     * @param nPositionDemandType
     * @param nIdRecord
     * @param nIdDirectory
     * @return */
     int getIdDemandType( int nPositionDemandType, int nIdRecord, int nIdDirectory );

    /**
     * Get the sms phone number
     * @param nPositionPhoneNumber
     * @param nIdRecord the id record
     * @param nIdDirectory the id directory
     * @return the sms phone number
     */

    String getSMSPhoneNumber( int nPositionPhoneNumber, int nIdRecord, int nIdDirectory );

  
    /**
     * Get the user guid
     * @param nPositionUserGuid
     * @param nIdRecord the id record
     * @param nIdDirectory the id directory
     * @return the user guid, an empty string if the position is not set
     */

    String getUserGuid( int nPositionUserGuid , int nIdRecord, int nIdDirectory );

    /**
     * @param nPosition
     * @param nIdRecord
     * @param nIdDirectory
     * @return */
     public String getRecordFieldValue( int nPosition, int nIdRecord, int nIdDirectory );

 
    /**
     * Gets the locale.
     *
     * @param request the request
     * @return the locale
     */    
    
    Locale getLocale( HttpServletRequest request );
    
    
    
}
