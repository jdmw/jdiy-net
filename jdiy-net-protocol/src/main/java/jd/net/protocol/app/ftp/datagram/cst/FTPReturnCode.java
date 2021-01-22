/**
 * 
 */
package jd.net.protocol.app.ftp.datagram.cst;

/**
 * @author jdmw
 *
 */
public class FTPReturnCode {

		
	/**
	 * FTP server return codes always have three digits, and each digit has a special meaning.[1] The first digit denotes whether the response is good, bad or incomplete:
	 
	The second digit is a grouping digit and encodes the following information:

	Range	Purpose
	x0x	Syntax
	These replies refer to syntax errors, syntactically correct commands that don't fit any functional category, unimplemented or superfluous commands.

	x1x	Information
	These are replies to requests for information, such as status or help.

	x2x	Connections
	Replies referring to the control and data connections.

	x3x	Authentication and accounting
	Replies for the login process and accounting procedures.

	x4x	Unspecified as of RFC 959.
	x5x	File system
	These replies indicate the status of the Server file system vis-a-vis the requested transfer or other file system action.

	Below is a list of all known return codes that may be issued by an FTP server.
		
	*/
	
	/**************************************************************************************
	 * 1xx	Positive Preliminary reply
	 * The requested action is being initiated, expect another reply before proceeding with a new command.
	 * 
	 *	The requested action is being initiated; expect another reply before proceeding with a new command. 
	 *  (The user-process sending another command before the completion reply would be in violation of protocol; 
	 *  but server-FTP processes should queue any commands that arrive while a preceding command is in progress.) 
	 *  This type of reply can be used to indicate that the command was accepted and the user-process may now 
	 *  pay attention to the data connections, for implementations where simultaneous monitoring is difficult. 
	 *  The server-FTP process may send at most, one 1xx reply per command.
	 *************************************************************************************/


	/**
	 * Restart marker replay . 
	 * In this case, the text is exact and not left to the particular implementation; 
	 * it must read: MARK yyyy = mmmm where yyyy is User-process data stream marker, 
	 * and mmmm server's equivalent marker (note the spaces between markers and "=").
	 */
	public final static String PRELIMINARY_RESTART = "110";

	/**
	 * Service ready in nnn minutes.
	 */
	public final static String PRELIMINARY_READY = "120";

	/**
	 * Data connection already open; transfer starting.
	 */
	public final static String PRELIMINARY_CONNETION_ALREADY_OPEN = "125";

	/**
	 * File status okay; about to open data connection.
	 */
	public final static String PRELIMINARY_FILE_OK = "150";

	
	/**************************************************************************************
	 * 2xx Positive Completion reply
	 * The requested action has been successfully completed. A new request may be initiated.
	 *************************************************************************************/
	
	/**
	 * The requested action has been successfully completed.
	 */
	public final static String OK = "200";

	/**
	 * Command not implemented, superfluous at this site.
	 */
	public final static String NOT_IMPLEMENTED = "202";

	/**
	 * System status, or system help reply.
	 */
	public final static String SYSTEM_STATUS = "211";

	/**
	 * Directory status.
	 */
	public final static String DIR_STATUS = "212";

	/**
	 * File status.
	 */
	public final static String FILE_STATUS = "213";

	/**
	 * Help message. Explains how to use the server or the meaning of a particular non-standard command. This reply is useful only to the human user.
	 */
	public final static String HELP_MSG = "214";

	/**
	 * NAME system type. Where NAME is an official system name from the registry kept by IANA.
	 */
	public final static String NAME_SYS_TYPE = "215";

	/**
	 * Service ready for new user.
	 */
	public final static String READY_FOR_NEW_USER = "220";

	/**
	 * Service closing control connection.
	 */
	public final static String CLOSING_CTRL_CONNECTION = "221";

	/**
	 * Data connection open; no transfer in progress.
	 */
	public final static String DATA_CONNECTION_OPEN = "225";

	/**
	 * Closing data connection. Requested file action successful (for example, file transfer or file abort).
	 */
	public final static String CLOSING_DATA_CONNECTION = "226";

	/**
	 * Entering Passive Mode (h1,h2,h3,h4,p1,p2).
	 */
	public final static String ENTER_PASSIVE_MODE = "227";

	/**
	 * Entering Long Passive Mode (long address, port).
	 */
	public final static String ENTER_LONG_PASSIVE_MODE = "228";

	/**
	 * Entering Extended Passive Mode (|||port|).
	 */
	public final static String ENTER_EXTENDED_PASSIVE_MODE = "229";

	/**
	 * User logged in, proceed. Logged out if appropriate.
	 */
	public final static String LOGGED_IN = "230";

	/**
	 * User logged out; service terminated.
	 */
	public final static String LOGGED_OUT = "231";

	/**
	 * Logout command noted, will complete when transfer done.
	 */
	public final static String LOGOUT_NOTED = "232";

	/**
	 * Specifies that the server accepts the authentication mechanism specified by the client, 
	 * and the exchange of security data is complete. A higher level nonstandard code created by Microsoft.
	 */
	public final static String AUTHENTICATION_MECHANISM = "234";

	/**
	 * Requested file action okay, completed.
	 */
	public final static String FILE_OK = "250";

	/**
	 * "PATHNAME" created.
	 */
	public final static String PATHNAME_CREATED = "257";

	
	/***************************************************************************************
	 * 3xx	Positive Intermediate reply
	 * The command has been accepted, but the requested action is being held in abeyance, 
	 * pending receipt of further information. The user should send another command specifying this information. 
	 * This reply is used in command sequence groups.
	 **************************************************************************************/
	
	/**
	 * User name okay, need password.
	 */
	public final static String NAME_OK = "331";

	/**
	 * Need account for login.
	 */
	public final static String NEED_ACCOUNT = "332";

	/**
	 * Requested file action pending further information
	 */
	public final static String REQUESTED_FILE = "350";

	/***************************************************************************************
	 * 4xx	Transient Negative Completion reply
	 * 
	 * The command was not accepted and the requested action did not take place, but the error condition 
	 * is temporary and the action may be requested again. The user should return to the beginning of the command sequence, 
	 * if any. It is difficult to assign a meaning to "transient", particularly when two distinct sites 
	 * (Server- and User-processes) have to agree on the interpretation. Each reply in the 4xx category might 
	 * have a slightly different time value, but the intent is that the user-process is encouraged to try again. 
	 * A rule of thumb in determining if a reply fits into the 4xx or the 5xx (Permanent Negative) category is 
	 * that replies are 4xx if the commands can be repeated without any change in command form or in properties of
	 *  the User or Server (e.g., the command is spelled the same with the same arguments used; 
	 *  the user does not change his file access or user name; the server does not put up a new implementation.)
	 **************************************************************************************/

	/**
	 * Service not available, closing control connection. This may be a reply to any command if the service knows it must shut down.
	 */
	public final static String NEG_SERVICE_NOT_AVAILABLE = "421";

	/**
	 * Can't open data connection.
	 */
	public final static String NEG_OPEN_DATA_CONN_FAIL= "425";

	/**
	 * Connection closed; transfer aborted.
	 */
	public final static String NEG_CONN_CLOSED = "426";

	/**
	 * Invalid username or password
	 */
	public final static String NEG_INVALIED_USER_OR_PASS = "430";

	/**
	 * Requested host unavailable.
	 */
	public final static String NEG_HOST_UNAVAILABLE = "434";

	/**
	 * Requested file action not taken.
	 */
	public final static String NEG_FILE_NOT_TAKEN = "450";

	/**
	 * Requested action aborted. Local error in processing.
	 */
	public final static String NEG_LOCAL_ERROR = "451";

	/**
	 * Requested action not taken. Insufficient storage space in system.File unavailable (e.g., file busy).
	 */
	public final static String NEG_INSUFFICIENT_STORAGE_SPACE = "452";

	
	/***************************************************************************************
	 * 5xx	Permanent Negative Completion reply
	 * The command was not accepted and the requested action did not take place. The User-process is discouraged 
	 * from repeating the exact request (in the same sequence). Even some "permanent" error conditions can be corrected, 
	 * so the human user may want to direct his User-process to reinitiate the command sequence by direct action 
	 * at some point in the future (e.g., after the spelling has been changed, or the user has altered his directory status.)
	***************************************************************************************/

	/**
	 * Syntax error in parameters or arguments.
	 */
	public final static String ERROR_INVALID_ARG = "501";

	/**
	 * Command not implemented.
	 */
	public final static String ERROR_COMMAND_NOT_IMPLEMENTED = "502";

	/**
	 * Bad sequence of commands.
	 */
	public final static String ERROR_BAD_SEQUENCE = "503";

	/**
	 * Command not implemented for that parameter.
	 */
	public final static String ERROR_NOT_COMPLENTED_FOR_PARAM = "504";

	/**
	 * Not logged in.
	 */
	public final static String ERROR_UNLOGIN = "530";

	/**
	 * Need account for storing files.
	 */
	public final static String ERROR_BEED_ACCOUNT_FOR_STORING_FILE = "532";

	/**
	 * Could Not Connect to Server - Policy Requires SSL
	 */
	public final static String ERROR_REQUIRE_SSL = "534";

	/**
	 * Requested action not taken. File unavailable (e.g., file not found, no access).
	 */
	public final static String ERROR_FILE_UNAVAILABLE = "550";

	/**
	 * Requested action aborted. Page type unknown.
	 */
	public final static String ERROR_PAGE_TYPE_UNKNOWN = "551";

	/**
	 * Requested file action aborted. Exceeded storage allocation (for current directory or dataset).
	 */
	public final static String ERROR_EXCEEDED_STORAGE = "552";

	/**
	 * Requested action not taken. File name not allowed.
	 */
	public final static String ERROR_FILE_NAME_NOT_ALLOWED = "553";


	/***************************************************************************************
	 * 6xx	Protected reply
	 * Replies regarding confidentiality and integrity
	 * 
	 * 
	 * The RFC 2228 introduced the concept of protected replies to increase security over the FTP communications. 
	 * The 6xx replies are Base64 encoded protected messages that serves as responses to secure commands. 
	 * When properly decoded, these replies fall into the above categories.
	 * ***************************************************************************************/
	
	/**
	 * Integrity protected reply.
	 */
	public final static String PROTECTED_INTEGRITY = "631";

	/**
	 * Confidentiality and integrity protected reply.
	 */
	public final static String PROTECTED_CONFIDENT_AND_INTEGRITY = "632";

	/**
	 * Confidentiality protected reply.
	 */
	public final static String PROTECTED_CONFIDENT = "633";

	/***************************************************************************************
	 * 1xxxx Series Common Winsock Error Codes
	 **************************************************************************************/

	/**
	 * Connection reset by peer. The connection was forcibly closed by the remote host.
	 */
	public final static String WINSOCK_ERROR_REMOTE_CLOSE = "10054";

	/**
	 * Cannot connect to remote server.
	 */
	public final static String WINSOCK_ERROR_CONNECT_FAILED = "10060";

	/**
	 * Cannot connect to remote server. The connection is actively refused by the server.
	 */
	public final static String WINSOCK_ERROR_REMOTE_REFUSED = "10061";

	/**
	 * Directory not empty.
	 */
	public final static String WINSOCK_ERROR_DIR_NOT_EMPTY = "10066";

	/**
	 * Too many users, server is full.
	 */
	public final static String WINSOCK_ERROR_TOO_MAMY_USERS = "10068";
		
	
}
