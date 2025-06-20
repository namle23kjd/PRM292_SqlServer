﻿namespace PRM292_SqlServerAPI.Model
{
    public class User
    {
        public int Id { get; set; }
        public string Name { get; set; }
        public string Email { get; set; }
        public string PhoneNumber { get; set; }
        public string Address { get; set; }
        public DateTime? DateOfBirth { get; set; }

        // Foreign key + navigation
        public int RoleId { get; set; }
        public Role Role { get; set; }
    }

}
