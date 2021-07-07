using System;
using System.IO;
using System.Windows.Forms;
using OpenCvSharp;
using OpenCvSharp.Extensions;
using System.Threading;
using System.Drawing;

namespace Face_acqusition
{
    public partial class CaptureLayout : Form
    {
        VideoCapture vc;
        VideoWriter vw = new VideoWriter();

        private string name;
        const string path = @"C:\sclab\";

        public CaptureLayout(string name)
        {
            InitializeComponent();
            this.CenterToScreen();

            this.name = name;
            DirectoryInfo di = new DirectoryInfo(path + name);

            if (di.Exists == false)
            {
                di.Create();
            }
        }

        private void CaptureLayout_Load(object sender, EventArgs e)
        {
            timer.Enabled = false;

            try
            {
                vc.Dispose();
            }
            catch { }

            vc = new VideoCapture(0);
            timer.Enabled = true;
        }

        private void timer_Tick(object sender, EventArgs e)
        {
            Mat frame = new Mat();
            vc.Read(frame);

            if (frame.Empty()) { return; }
            else
            {
                try
                {
                    this.Invoke((Action)(() =>
                    {
                        vw.Write(frame);
                        live_box.Image = BitmapConverter.ToBitmap(frame);
                    }), null);
                }
                catch (Exception ex)
                {
                    MessageBox.Show("timer_Tick() error");
                    Console.WriteLine(ex.Message);
                    Console.WriteLine(ex.StackTrace);
                }
            }
        }

        private void capture_btn_Click(object sender, EventArgs e)
        {
            string cur_name = DateTime.Now.ToString("yyyyMMdd-HHmmss");
            string cur_file = path + name + @"\" + cur_name + ".png";
            try
            {
                live_box.Image.Save(cur_file, System.Drawing.Imaging.ImageFormat.Png);
            }
            catch (Exception ex)
            {
                MessageBox.Show("caputure_btn_Click() error");
                Console.WriteLine(ex.Message);
                Console.WriteLine(ex.StackTrace);
            }

            string cur_msg = "Do you want to save temporary photos?\n Save when you click 'Yes',\n Don't save when you click 'No'.";
            if (MessageBox.Show(cur_msg, "", MessageBoxButtons.YesNo) == DialogResult.Yes)
            {
                MessageBox.Show("Save !");
            }
            else
            {
                if (File.Exists(cur_file))
                {
                    File.Delete(cur_file);
                }
                MessageBox.Show("Delete !");
            }
        }

        private void CaptureLayout_FormClosing(object sender, FormClosingEventArgs e)
        {

        }
    }
}