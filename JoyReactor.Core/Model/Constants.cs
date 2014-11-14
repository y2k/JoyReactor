using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model
{
    static class Constants
    {
        /// <summary>
        /// Время устаревания страницы списка постов (милисекунды)
        /// </summary>
        public const long TagLifeTime = 5 * 60 * 1000; // 5 минут (для отладки)

        /// <summary>
        /// Время устаревания содержимого поста (в милисекундах)
        /// </summary>
        public const long PostListTime = 5 * 60 * 1000; // 5 минут (для отладки)
    }
}