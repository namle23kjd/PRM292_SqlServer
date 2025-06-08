using Microsoft.EntityFrameworkCore;
using System.Collections.Generic;

namespace PRM292_SqlServerAPI.Model
{
    public class AppDbContext : DbContext
    {
        public AppDbContext(DbContextOptions<AppDbContext> options) : base(options) { }
        public DbSet<User> Users { get; set; }
    }

}
