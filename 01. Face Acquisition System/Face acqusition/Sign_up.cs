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
    public partial class Sign_up : Form
    {
        string dbServer;
        string dbUid;
        string dbPwd;

        string dbName;
        string dbInfo;


        public Sign_up()
        {
            InitializeComponent();
            this.CenterToScreen();

            Database_Setting();
            string db_state = Select_Member();

            if (!db_state.Equals("true"))
            {
                MessageBox.Show(db_state);
            }
        }

        private void Database_Setting()
        {
            dbServer = "localhost";
            dbUid = "root";
            dbPwd = "lht1080";

            dbName = "SCLAB";
            dbInfo = "Data Source=" + dbServer + ";" + "Database=" + dbName + ";" + "User Id=" + dbUid + ";" + "Password=" + dbPwd + ";charset=euckr";
        }

        private string Select_Member()
        {
            MySqlConnection conn;

            using (conn = new MySqlConnection(dbInfo))
            {
                if (conn.State != ConnectionState.Open)
                {
                    conn.Open();
                }

                try
                {
                    string sql = "SELECT * FROM MEMBER";
                    MySqlCommand cmd = new MySqlCommand(sql, conn);
                    MySqlDataReader rdr = cmd.ExecuteReader();

                    while (rdr.Read())
                    {
                        string cur_name = Convert.ToString(rdr["NAME"].ToString());
                        member_list.Items.Add(cur_name);
                    }
                }
                catch (Exception ex)
                {
                    Console.WriteLine(ex.Message);
                    Console.WriteLine(ex.StackTrace);

                    return "SELECT error";
                }

                conn.Close();
            }

            return "true";
        }

        private void run_btn_Click(object sender, EventArgs e)
        {
            if (member_list.Text.ToString().Any(x => Char.IsWhiteSpace(x) == true) || String.IsNullOrEmpty(member_list.Text))
            {
                MessageBox.Show("Please choose the correct name.");
            }

            else
            {
                CaptureLayout capture = new CaptureLayout(member_list.Text);
                capture.Show();
            }
        }
    }
}
