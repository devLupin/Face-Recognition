﻿using MySql.Data.MySqlClient;
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
        }

        public string IsEmpty()
        {
            Database_Setting();
            return Select_Member();
        }

        private void Sign_up_Load(object sender, EventArgs e)
        {
            // autocomplete
            this.member_list.AutoCompleteMode = AutoCompleteMode.SuggestAppend;
            this.member_list.AutoCompleteSource = AutoCompleteSource.ListItems;
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

                        if (cur_name.Equals("DUMMY")) { continue; }

                        cur_name = cur_name.Replace("_", " ");
                        member_list.Items.Add(cur_name);
                    }

                    conn.Close();
                    return "true";
                }
                catch (Exception ex)
                {
                    Console.WriteLine(ex.Message);
                    Console.WriteLine(ex.StackTrace);

                    conn.Close();
                    return "SELECT error";
                }
            }
        }

        private void run_btn_Click(object sender, EventArgs e)
        {
            string cur_msg = "Is the selected " + "'" + member_list.Text + "'" + " correct?";
            if (MessageBox.Show(cur_msg, "", MessageBoxButtons.YesNo) == DialogResult.Yes)
            {
                string folder_name = member_list.Text.Replace(" ", "_");

                if (folder_name.Any(x => Char.IsWhiteSpace(x) == true) || String.IsNullOrEmpty(folder_name))
                {
                    MessageBox.Show("Please choose the correct name.");
                }
                else
                {
                    CaptureLayout capture = new CaptureLayout(folder_name);
                    capture.Show();
                }
            }
            else { }
        }
    }
}