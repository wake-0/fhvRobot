using System;
using System.Globalization;
using System.Windows.Controls;
using System.Windows.Data;

namespace GameServer.Converters
{
    public class IndexConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            var item = value as ListViewItem;
            var listView = ItemsControl.ItemsControlFromItemContainer(item) as ListView;
            if (item == null || listView == null) return null;

            var offset = 0;
            if (parameter != null)
            {
                int.TryParse(parameter as string, out offset);
            }

            var index = listView.ItemContainerGenerator.IndexFromContainer(item) + offset;
            return index.ToString() + ".";
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            throw new NotImplementedException();
        }
    }
}
