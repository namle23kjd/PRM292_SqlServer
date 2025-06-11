using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using PRM292_SqlServerAPI.Model;
using System.ComponentModel.DataAnnotations;

namespace PRM292_SqlServerAPI.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class CRUD : ControllerBase
    {
        private readonly AppDbContext _context;

        public CRUD(AppDbContext context)
        {
            _context = context;
        }

        // ========== DTOs ==========
        public class UserDto
        {
            public int Id { get; set; }
            public string Name { get; set; }
            public string Email { get; set; }
            public string PhoneNumber { get; set; }
            public string Address { get; set; }
            public DateTime? DateOfBirth { get; set; }
            public int RoleId { get; set; }
            public string RoleName { get; set; }
        }

        public class CreateUserDto
        {
            [Required]
            public string Name { get; set; }
            [Required]
            [EmailAddress]
            public string Email { get; set; }
            public string PhoneNumber { get; set; }
            public string Address { get; set; }
            public DateTime? DateOfBirth { get; set; }
            [Required]
            public int RoleId { get; set; }
        }

        public class UpdateUserDto
        {
            [Required]
            public string Name { get; set; }
            [Required]
            [EmailAddress]
            public string Email { get; set; }
            public string PhoneNumber { get; set; }
            public string Address { get; set; }
            public DateTime? DateOfBirth { get; set; }
            [Required]
            public int RoleId { get; set; }
        }

        public class RoleDto
        {
            public int Id { get; set; }
            public string RoleName { get; set; }
            public int UserCount { get; set; }
        }

        public class CreateRoleDto
        {
            [Required]
            public string RoleName { get; set; }
        }

        public class UpdateRoleDto
        {
            [Required]
            public string RoleName { get; set; }
        }

        public class ApiResponse<T>
        {
            public bool Success { get; set; }
            public string Message { get; set; }
            public T Data { get; set; }
        }

        // ========== USER CRUD OPERATIONS ==========

        // GET: api/CRUD/users
        [HttpGet("users")]
        public async Task<ActionResult<ApiResponse<IEnumerable<UserDto>>>> GetAllUsers()
        {
            try
            {
                var users = await _context.Users
                    .Include(u => u.Role)
                    .Select(u => new UserDto
                    {
                        Id = u.Id,
                        Name = u.Name,
                        Email = u.Email,
                        PhoneNumber = u.PhoneNumber,
                        Address = u.Address,
                        DateOfBirth = u.DateOfBirth,
                        RoleId = u.RoleId,
                        RoleName = u.Role.RoleName
                    })
                    .ToListAsync();

                return Ok(new ApiResponse<IEnumerable<UserDto>>
                {
                    Success = true,
                    Message = "Users retrieved successfully",
                    Data = users
                });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new ApiResponse<IEnumerable<UserDto>>
                {
                    Success = false,
                    Message = $"Error retrieving users: {ex.Message}",
                    Data = null
                });
            }
        }

        // GET: api/CRUD/users/5
        [HttpGet("users/{id}")]
        public async Task<ActionResult<ApiResponse<UserDto>>> GetUser(int id)
        {
            try
            {
                var user = await _context.Users
                    .Include(u => u.Role)
                    .Where(u => u.Id == id)
                    .Select(u => new UserDto
                    {
                        Id = u.Id,
                        Name = u.Name,
                        Email = u.Email,
                        PhoneNumber = u.PhoneNumber,
                        Address = u.Address,
                        DateOfBirth = u.DateOfBirth,
                        RoleId = u.RoleId,
                        RoleName = u.Role.RoleName
                    })
                    .FirstOrDefaultAsync();

                if (user == null)
                {
                    return NotFound(new ApiResponse<UserDto>
                    {
                        Success = false,
                        Message = $"User with ID {id} not found",
                        Data = null
                    });
                }

                return Ok(new ApiResponse<UserDto>
                {
                    Success = true,
                    Message = "User retrieved successfully",
                    Data = user
                });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new ApiResponse<UserDto>
                {
                    Success = false,
                    Message = $"Error retrieving user: {ex.Message}",
                    Data = null
                });
            }
        }

        // POST: api/CRUD/users
        [HttpPost("users")]
        public async Task<ActionResult<ApiResponse<UserDto>>> CreateUser(CreateUserDto createUserDto)
        {
            try
            {
                // Validate role exists
                var roleExists = await _context.Roles.AnyAsync(r => r.Id == createUserDto.RoleId);
                if (!roleExists)
                {
                    return BadRequest(new ApiResponse<UserDto>
                    {
                        Success = false,
                        Message = "Selected role does not exist",
                        Data = null
                    });
                }

                var user = new User
                {
                    Name = createUserDto.Name,
                    Email = createUserDto.Email,
                    PhoneNumber = createUserDto.PhoneNumber,
                    Address = createUserDto.Address,
                    DateOfBirth = createUserDto.DateOfBirth,
                    RoleId = createUserDto.RoleId
                };

                _context.Users.Add(user);
                await _context.SaveChangesAsync();

                // Retrieve the created user with role information
                var createdUser = await _context.Users
                    .Include(u => u.Role)
                    .Where(u => u.Id == user.Id)
                    .Select(u => new UserDto
                    {
                        Id = u.Id,
                        Name = u.Name,
                        Email = u.Email,
                        PhoneNumber = u.PhoneNumber,
                        Address = u.Address,
                        DateOfBirth = u.DateOfBirth,
                        RoleId = u.RoleId,
                        RoleName = u.Role.RoleName
                    })
                    .FirstOrDefaultAsync();

                return CreatedAtAction(nameof(GetUser), new { id = user.Id }, new ApiResponse<UserDto>
                {
                    Success = true,
                    Message = "User created successfully",
                    Data = createdUser
                });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new ApiResponse<UserDto>
                {
                    Success = false,
                    Message = $"Error creating user: {ex.Message}",
                    Data = null
                });
            }
        }

        // PUT: api/CRUD/users/5
        [HttpPut("users/{id}")]
        public async Task<ActionResult<ApiResponse<UserDto>>> UpdateUser(int id, UpdateUserDto updateUserDto)
        {
            try
            {
                var user = await _context.Users.FindAsync(id);
                if (user == null)
                {
                    return NotFound(new ApiResponse<UserDto>
                    {
                        Success = false,
                        Message = $"User with ID {id} not found",
                        Data = null
                    });
                }

                // Validate role exists
                var roleExists = await _context.Roles.AnyAsync(r => r.Id == updateUserDto.RoleId);
                if (!roleExists)
                {
                    return BadRequest(new ApiResponse<UserDto>
                    {
                        Success = false,
                        Message = "Selected role does not exist",
                        Data = null
                    });
                }

                user.Name = updateUserDto.Name;
                user.Email = updateUserDto.Email;
                user.PhoneNumber = updateUserDto.PhoneNumber;
                user.Address = updateUserDto.Address;
                user.DateOfBirth = updateUserDto.DateOfBirth;
                user.RoleId = updateUserDto.RoleId;

                await _context.SaveChangesAsync();

                // Retrieve the updated user with role information
                var updatedUser = await _context.Users
                    .Include(u => u.Role)
                    .Where(u => u.Id == id)
                    .Select(u => new UserDto
                    {
                        Id = u.Id,
                        Name = u.Name,
                        Email = u.Email,
                        PhoneNumber = u.PhoneNumber,
                        Address = u.Address,
                        DateOfBirth = u.DateOfBirth,
                        RoleId = u.RoleId,
                        RoleName = u.Role.RoleName
                    })
                    .FirstOrDefaultAsync();

                return Ok(new ApiResponse<UserDto>
                {
                    Success = true,
                    Message = "User updated successfully",
                    Data = updatedUser
                });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new ApiResponse<UserDto>
                {
                    Success = false,
                    Message = $"Error updating user: {ex.Message}",
                    Data = null
                });
            }
        }

        // DELETE: api/CRUD/users/5
        [HttpDelete("users/{id}")]
        public async Task<ActionResult<ApiResponse<object>>> DeleteUser(int id)
        {
            try
            {
                var user = await _context.Users.FindAsync(id);
                if (user == null)
                {
                    return NotFound(new ApiResponse<object>
                    {
                        Success = false,
                        Message = $"User with ID {id} not found",
                        Data = null
                    });
                }

                _context.Users.Remove(user);
                await _context.SaveChangesAsync();

                return Ok(new ApiResponse<object>
                {
                    Success = true,
                    Message = "User deleted successfully",
                    Data = null
                });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new ApiResponse<object>
                {
                    Success = false,
                    Message = $"Error deleting user: {ex.Message}",
                    Data = null
                });
            }
        }

        // ========== ROLE CRUD OPERATIONS ==========

        // GET: api/CRUD/roles
        [HttpGet("roles")]
        public async Task<ActionResult<ApiResponse<IEnumerable<RoleDto>>>> GetAllRoles()
        {
            try
            {
                var roles = await _context.Roles
                    .Select(r => new RoleDto
                    {
                        Id = r.Id,
                        RoleName = r.RoleName,
                        UserCount = r.Users.Count()
                    })
                    .ToListAsync();

                return Ok(new ApiResponse<IEnumerable<RoleDto>>
                {
                    Success = true,
                    Message = "Roles retrieved successfully",
                    Data = roles
                });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new ApiResponse<IEnumerable<RoleDto>>
                {
                    Success = false,
                    Message = $"Error retrieving roles: {ex.Message}",
                    Data = null
                });
            }
        }

        // GET: api/CRUD/roles/5
        [HttpGet("roles/{id}")]
        public async Task<ActionResult<ApiResponse<RoleDto>>> GetRole(int id)
        {
            try
            {
                var role = await _context.Roles
                    .Where(r => r.Id == id)
                    .Select(r => new RoleDto
                    {
                        Id = r.Id,
                        RoleName = r.RoleName,
                        UserCount = r.Users.Count()
                    })
                    .FirstOrDefaultAsync();

                if (role == null)
                {
                    return NotFound(new ApiResponse<RoleDto>
                    {
                        Success = false,
                        Message = $"Role with ID {id} not found",
                        Data = null
                    });
                }

                return Ok(new ApiResponse<RoleDto>
                {
                    Success = true,
                    Message = "Role retrieved successfully",
                    Data = role
                });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new ApiResponse<RoleDto>
                {
                    Success = false,
                    Message = $"Error retrieving role: {ex.Message}",
                    Data = null
                });
            }
        }

        // POST: api/CRUD/roles
        [HttpPost("roles")]
        public async Task<ActionResult<ApiResponse<RoleDto>>> CreateRole(CreateRoleDto createRoleDto)
        {
            try
            {
                var role = new Role
                {
                    RoleName = createRoleDto.RoleName
                };

                _context.Roles.Add(role);
                await _context.SaveChangesAsync();

                var createdRole = new RoleDto
                {
                    Id = role.Id,
                    RoleName = role.RoleName,
                    UserCount = 0
                };

                return CreatedAtAction(nameof(GetRole), new { id = role.Id }, new ApiResponse<RoleDto>
                {
                    Success = true,
                    Message = "Role created successfully",
                    Data = createdRole
                });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new ApiResponse<RoleDto>
                {
                    Success = false,
                    Message = $"Error creating role: {ex.Message}",
                    Data = null
                });
            }
        }

        // PUT: api/CRUD/roles/5
        [HttpPut("roles/{id}")]
        public async Task<ActionResult<ApiResponse<RoleDto>>> UpdateRole(int id, UpdateRoleDto updateRoleDto)
        {
            try
            {
                var role = await _context.Roles.FindAsync(id);
                if (role == null)
                {
                    return NotFound(new ApiResponse<RoleDto>
                    {
                        Success = false,
                        Message = $"Role with ID {id} not found",
                        Data = null
                    });
                }

                role.RoleName = updateRoleDto.RoleName;
                await _context.SaveChangesAsync();

                var updatedRole = await _context.Roles
                    .Where(r => r.Id == id)
                    .Select(r => new RoleDto
                    {
                        Id = r.Id,
                        RoleName = r.RoleName,
                        UserCount = r.Users.Count()
                    })
                    .FirstOrDefaultAsync();

                return Ok(new ApiResponse<RoleDto>
                {
                    Success = true,
                    Message = "Role updated successfully",
                    Data = updatedRole
                });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new ApiResponse<RoleDto>
                {
                    Success = false,
                    Message = $"Error updating role: {ex.Message}",
                    Data = null
                });
            }
        }

        // DELETE: api/CRUD/roles/5
        [HttpDelete("roles/{id}")]
        public async Task<ActionResult<ApiResponse<object>>> DeleteRole(int id)
        {
            try
            {
                var role = await _context.Roles.Include(r => r.Users).FirstOrDefaultAsync(r => r.Id == id);
                if (role == null)
                {
                    return NotFound(new ApiResponse<object>
                    {
                        Success = false,
                        Message = $"Role with ID {id} not found",
                        Data = null
                    });
                }

                if (role.Users.Any())
                {
                    return BadRequest(new ApiResponse<object>
                    {
                        Success = false,
                        Message = "Cannot delete role that has users assigned to it",
                        Data = null
                    });
                }

                _context.Roles.Remove(role);
                await _context.SaveChangesAsync();

                return Ok(new ApiResponse<object>
                {
                    Success = true,
                    Message = "Role deleted successfully",
                    Data = null
                });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new ApiResponse<object>
                {
                    Success = false,
                    Message = $"Error deleting role: {ex.Message}",
                    Data = null
                });
            }
        }

        // ========== UTILITY ENDPOINTS ==========

        // GET: api/CRUD/users/by-role/5
        [HttpGet("users/by-role/{roleId}")]
        public async Task<ActionResult<ApiResponse<IEnumerable<UserDto>>>> GetUsersByRole(int roleId)
        {
            try
            {
                var users = await _context.Users
                    .Include(u => u.Role)
                    .Where(u => u.RoleId == roleId)
                    .Select(u => new UserDto
                    {
                        Id = u.Id,
                        Name = u.Name,
                        Email = u.Email,
                        PhoneNumber = u.PhoneNumber,
                        Address = u.Address,
                        DateOfBirth = u.DateOfBirth,
                        RoleId = u.RoleId,
                        RoleName = u.Role.RoleName
                    })
                    .ToListAsync();

                return Ok(new ApiResponse<IEnumerable<UserDto>>
                {
                    Success = true,
                    Message = "Users retrieved successfully",
                    Data = users
                });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new ApiResponse<IEnumerable<UserDto>>
                {
                    Success = false,
                    Message = $"Error retrieving users by role: {ex.Message}",
                    Data = null
                });
            }
        }

        // GET: api/CRUD/search/users?name=john&email=example
        [HttpGet("search/users")]
        public async Task<ActionResult<ApiResponse<IEnumerable<UserDto>>>> SearchUsers(string name = null, string email = null)
        {
            try
            {
                var query = _context.Users.Include(u => u.Role).AsQueryable();

                if (!string.IsNullOrEmpty(name))
                {
                    query = query.Where(u => u.Name.Contains(name));
                }

                if (!string.IsNullOrEmpty(email))
                {
                    query = query.Where(u => u.Email.Contains(email));
                }

                var users = await query
                    .Select(u => new UserDto
                    {
                        Id = u.Id,
                        Name = u.Name,
                        Email = u.Email,
                        PhoneNumber = u.PhoneNumber,
                        Address = u.Address,
                        DateOfBirth = u.DateOfBirth,
                        RoleId = u.RoleId,
                        RoleName = u.Role.RoleName
                    })
                    .ToListAsync();

                return Ok(new ApiResponse<IEnumerable<UserDto>>
                {
                    Success = true,
                    Message = "Search completed successfully",
                    Data = users
                });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new ApiResponse<IEnumerable<UserDto>>
                {
                    Success = false,
                    Message = $"Error searching users: {ex.Message}",
                    Data = null
                });
            }
        }

        // GET: api/CRUD/dashboard/stats
        [HttpGet("dashboard/stats")]
        public async Task<ActionResult<ApiResponse<object>>> GetDashboardStats()
        {
            try
            {
                var totalUsers = await _context.Users.CountAsync();
                var totalRoles = await _context.Roles.CountAsync();
                var usersWithoutRole = await _context.Users.CountAsync(u => u.Role == null);
                var rolesWithUsers = await _context.Roles.CountAsync(r => r.Users.Any());

                var stats = new
                {
                    TotalUsers = totalUsers,
                    TotalRoles = totalRoles,
                    UsersWithoutRole = usersWithoutRole,
                    RolesWithUsers = rolesWithUsers,
                    RolesWithoutUsers = totalRoles - rolesWithUsers
                };

                return Ok(new ApiResponse<object>
                {
                    Success = true,
                    Message = "Dashboard stats retrieved successfully",
                    Data = stats
                });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new ApiResponse<object>
                {
                    Success = false,
                    Message = $"Error retrieving dashboard stats: {ex.Message}",
                    Data = null
                });
            }
        }
    }
}