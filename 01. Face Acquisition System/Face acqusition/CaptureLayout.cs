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
                        pictureBox1.Image = BitmapConverter.ToBitmap(frame);
                    }), null);
                }
                catch { }
            }
        }

        private void capture_btn_Click(object sender, EventArgs e)
        {
            string cur_filename = DateTime.Now.ToString("yyyyMMdd-HHmmss");
            try
            {
                pictureBox1.Image.Save(path + name + @"\" + cur_filename + ".png", System.Drawing.Imaging.ImageFormat.Png);
                MessageBox.Show("save !");
            }
            catch { }
        }

        private void saved_photo_btn_Click(object sender, EventArgs e)
        {
            string folder_path = path + name;
            System.Diagnostics.Process.Start(folder_path);
        }

        private void CaptureLayout_FormClosing(object sender, FormClosingEventArgs e)
        {

        }
    }
}