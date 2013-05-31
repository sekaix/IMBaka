/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

/**
 *
 * @author Edgar
 */
public class Account
{

    public String username;
    public byte[] password;

    public Account(String username, byte[] password)
    {
        this.username = username;
        this.password = password;
    }
}
