using MySql.Data.MySqlClient;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace Face_acqusition
{
    public partial class Sign_in : Form
    {
        string dbServer;
        string dbUid;
        string dbPwd;

        string dbName;
        string dbInfo;


        public Sign_in()
        {
            InitializeComponent();
            this.CenterToScreen();

            contents.Text =
                "Let's start acquiring faces." + "\n\n" +
                "Data is stored and archived on the server." + "\n\n" +
                "Collected faces are used for research purposes." + "\n\n";

            first_name_txt.Text = string.Empty;
            last_name_txt.Text = string.Empty;
        }

        bool IsEnglish(char ch)
        {
            if ((0x61 <= ch && ch <= 0x7A) || (0x41 <= ch && ch <= 0x5A)
                || (ch == '_'))     // Exception '_'
                return true;

            else
                return false;
        }

        private void Database_Setting()
        {
            dbServer = "localhost";
            dbUid = "root";
            dbPwd = "lht1080";

            dbName = "SCLAB";
            dbInfo = "Data Source=" + dbServer + ";" + "Database=" + dbName + ";" + "User Id=" + dbUid + ";" + "Password=" + dbPwd + ";charset=euckr";
        }

        private string Insert_Member(string name)
        {
            MySqlConnection conn;

            using (conn = new MySqlConnection(dbInfo))
            {
                if (conn.State != ConnectionState.Open)
                {
                    conn.Open();
                }

                
                try     // CREATE TABLE
                {
                    string sql =
                        "CREATE TABLE IF NOT EXISTS MEMBER(" +
                        "NAME VARCHAR(30) NOT NULL," +
                        "CODE INT NOT NULL," +
                        "PRIMARY KEY(CODE))";

                    MySqlCommand cmd = new MySqlCommand(sql, conn);
                    cmd.ExecuteNonQuery();
                }
                catch (Exception ex)
                {
                    Console.WriteLine("CREATE TABLE error!");
                    Console.WriteLine(ex.Message);
                    Console.WriteLine(ex.StackTrace);
                }

                try     // ADD DUMMY
                {
                    string sql =
                        "INSERT IGNORE INTO MEMBER VALUES('DUMMY', 1)";

                    MySqlCommand cmd = new MySqlCommand(sql, conn);
                    cmd.ExecuteNonQuery();
                }
                catch (Exception ex)
                {
                    Console.WriteLine("INSERT DUMMY error!");
                    Console.WriteLine(ex.Message);
                    Console.WriteLine(ex.StackTrace);
                }

                for (int i=0; i<name.Length; i++)
                {
                    if (!IsEnglish(name[i]))
                    {
                        return "Incorrect string value. Only English.";
                    }
                }

                int code = -1;
                try
                {
                    string sql = "SELECT CODE FROM MEMBER";
                    MySqlCommand cmd = new MySqlCommand(sql, conn);
                    MySqlDataReader rdr = cmd.ExecuteReader();

                    while (rdr.Read())
                    {
                        code = Convert.ToInt32(rdr["CODE"].ToString());
                    }
                    rdr.Close();
                }
                catch (Exception ex)
                {
                    Console.WriteLine(ex.Message);
                    Console.WriteLine(ex.StackTrace);
                    return "SELECT code error";
                }

                try
                {
                    string sql = "INSERT INTO MEMBER VALUES(" + "'" + name + "'," + (code + 1) + ")";
                    MySqlCommand cmd = new MySqlCommand(sql, conn);
                    cmd.ExecuteNonQuery();
                }
                catch (Exception ex)
                {
                    Console.WriteLine(ex.Message);
                    Console.WriteLine(ex.StackTrace);

                    return "Duplicate names exist.";
                }

                conn.Close();
            }

            return "true";
        }

        private void create_account_btn_Click(object sender, EventArgs e)
        {
            if (!Terms_and_Conditions.Checked)
            {
                MessageBox.Show("Check our terms and conditions.");
            }
            else
            {
                if ((first_name_txt.Text.ToString().Any(x => Char.IsWhiteSpace(x) == true) || String.IsNullOrEmpty(first_name_txt.Text)) &&
                    (last_name_txt.Text.ToString().Any(x => Char.IsWhiteSpace(x) == true) || String.IsNullOrEmpty(last_name_txt.Text)))
                {
                    MessageBox.Show("The name cannot contain spaces or be empty.");
                }
                else
                {
                    Database_Setting();

                    string name = first_name_txt.Text + "_" + last_name_txt.Text;
                    string db_state = Insert_Member(name);

                    if (db_state.Equals("true"))
                    {
                        MessageBox.Show("Please sign up from the previous screen.");
                        this.Close();
                    }
                    else
                    {
                        MessageBox.Show(db_state);
                    }
                }
            }
        }
    }
}
