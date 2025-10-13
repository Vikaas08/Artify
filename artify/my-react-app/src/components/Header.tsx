import { Link, useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../AuthContext";
import { useToast } from "../ToastContext";
import axios from "axios";
import { Nav, Navbar, Dropdown, Image } from "react-bootstrap";

function Header() {
  const { userData, logout } = useAuth();
  const { addToast } = useToast();
  const navigate = useNavigate();

  function handleAccountDelete() {
    if (userData) {
      console.log("handleAccountDelete called", userData.email);
      axios
        .post("https://hw3-cs571-spring-25.uw.r.appspot.com/api/delete", {
          email: userData.email,
        })
        .then(() => {
          logout();
          navigate("/");
          addToast("Account deleted", "danger");
        })
        .catch((err) => {
          console.log("error while logging out!", err);
        });
    }
  }

  function handleLogout() {
    axios
      .post(
        "https://hw3-cs571-spring-25.uw.r.appspot.com/api/logout",
        {},
        { withCredentials: true }
      )
      .then(() => {
        logout();
        navigate("/");
        addToast("Logged out", "success");
      })
      .catch((err) => {
        console.log("error while logging out!", err);
      });
  }

  const location = useLocation();
  return (
    <Navbar expand="sm" className="px-4 bg-body-tertiary">
      <Navbar.Brand as={Link} to="/">
        Artist Search
      </Navbar.Brand>
      <Navbar.Toggle aria-controls="basic-navbar-nav" />
      <Navbar.Collapse id="basic-navbar-nav">
        <Nav variant="pills" activeKey={location.pathname} className="ms-auto">
          <Nav.Item>
            <Nav.Link as={Link} to="/" eventKey="/">
              Search
            </Nav.Link>
          </Nav.Item>

          {!userData ? (
            <>
              <Nav.Item>
                <Nav.Link as={Link} to="/login" eventKey="/login">
                  Login
                </Nav.Link>
              </Nav.Item>
              <Nav.Item>
                <Nav.Link as={Link} to="/register" eventKey="/register">
                  Register
                </Nav.Link>
              </Nav.Item>
            </>
          ) : (
            <>
              <Nav.Item>
                <Nav.Link as={Link} to="/favourites" eventKey="/favourites">
                  Favourites
                </Nav.Link>
              </Nav.Item>

              <Dropdown align="end">
                <Dropdown.Toggle
                  as="a"
                  href="#"
                  className="d-block link-body-emphasis text-decoration-none"
                >
                  <Image
                    src={
                      userData
                        ? userData.profileImageUrl
                        : "https://github.com/mdo.png"
                    }
                    alt="User Avatar"
                    width={40}
                    height={35}
                    roundedCircle
                  />{" "}
                  {userData && userData.fullname}
                </Dropdown.Toggle>

                <Dropdown.Menu>
                  <Dropdown.Item
                    className="text-danger"
                    onClick={handleAccountDelete}
                  >
                    Delete account
                  </Dropdown.Item>
                  <Dropdown.Divider />
                  <Dropdown.Item
                    className="text-primary"
                    onClick={handleLogout}
                  >
                    Log out
                  </Dropdown.Item>
                </Dropdown.Menu>
              </Dropdown>
            </>
          )}
        </Nav>
      </Navbar.Collapse>
    </Navbar>
  );
}

export default Header;
