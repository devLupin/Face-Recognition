using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace Face_Recognition_System
{
    public partial class startLayout : Form
    {
        public startLayout()
        {
            InitializeComponent();
            this.CenterToScreen();
        }

        private void sign_in_btn_Click(object sender, EventArgs e)
        {
            Sign_in sign_in = new Sign_in();
            sign_in.Show();
        }

        private void sign_up_btn_Click(object sender, EventArgs e)
        {
            Sign_up sign_up = new Sign_up();
            sign_up.Show();
        }
    }
}
