import { Form, Button } from "react-bootstrap";
import { useNavigate, Link } from "react-router-dom";
import { useEffect, useState } from "react";
import axios from "axios";
import Header from "../components/Header";
import Footer from "../components/Footer";

const Register = () => {
  const [formData, setFormData] = useState({
    name: "",
    email: "",
    password: "",
  });
  const [validate, setValidate] = useState({
    name: "",
    email: "",
    password: "",
  });
  const [disable, setDisable] = useState(true);
  const [isChanged, setIsChanged] = useState(false);
  const navigate = useNavigate();

  const handleChange = (event: any) => {
    // const name = event.target.name;
    // if (name === "Fullname") {
    //   setName(event.target.value);
    // } else if (name === "Email") {
    //   setEmail(event.target.value);
    // } else if (name === "Password") {
    //   setPassword(event.target.value);
    // }
    const { name, value } = event.target;
    setIsChanged(true);
    setFormData((prevValue) => {
      return {
        ...prevValue,
        [name]: value,
      };
    });
    setValidate((prevValue) => {
      return {
        ...prevValue,
        [name]: "",
      };
    });
  };

  const checkErrors = () => {
    let hasErr = false;
    const { name, email, password } = formData;
    const newErrors = { name: "", email: "", password: "" };
    const pattern = new RegExp("^[\\w.-]+@([\\w-]+\\.)+[\\w-]{2,}$");

    if (!name || name === "") {
      newErrors.name = "Fullname is required.";
      hasErr = true;
    }
    if (!email || email === "") {
      newErrors.email = "Email is required.";
      hasErr = true;
    } else if (!pattern.test(email)) {
      newErrors.email = "Email must be valid.";
      hasErr = true;
    }

    if (!password || password === "") {
      newErrors.password = "Password is required.";
      hasErr = true;
    }

    setValidate(newErrors);
    return hasErr;
  };

  useEffect(() => {
    if (isChanged) setDisable(checkErrors());
  }, [formData, isChanged]);

  const handleSubmit = (event: any) => {
    event.preventDefault();

    const formErrors = checkErrors();
    if (!formErrors) {
      // console.log("Registeration form successfully validated");
      axios
        .post("https://hw3-cs571-spring-25.uw.r.appspot.com/api/register", {
          name: formData.name,
          email: formData.email,
          password: formData.password,
        })
        .then((response) => {
          // console.log("Registeration form submitted successfully", response);

          if (response.status === 204) {
            setValidate((prevValue) => {
              return {
                ...prevValue,
                email: "User with this email already exists",
              };
            });
          } else navigate("/login");
        })
        .catch((err) =>
          console.log("Error while sending the register form", err)
        );
    }
  };

  return (
    <div>
      <Header />
      <div className="container mt-5 mb-5">
        <div className="row justify-content-center">
          <div className="col-md-4">
            <div className="px-4 py-4 d-grid gap-2 border border-2 rounded">
              <Form noValidate onSubmit={handleSubmit}>
                <h1 className="pb-4">Register</h1>

                <Form.Group className="mb-2">
                  <Form.Label className="fw-semibold">Fullname</Form.Label>
                  <Form.Control
                    type="text"
                    name="name"
                    placeholder="John Doe"
                    required
                    onChange={handleChange}
                    value={formData.name}
                    isInvalid={!!validate.name}
                  />
                  <Form.Control.Feedback type="invalid">
                    {validate.name}
                  </Form.Control.Feedback>
                </Form.Group>

                <Form.Group className="mb-2">
                  <Form.Label className="fw-semibold">Email address</Form.Label>
                  <Form.Control
                    type="email"
                    name="email"
                    placeholder="Enter email"
                    required
                    onChange={handleChange}
                    value={formData.email}
                    isInvalid={!!validate.email}
                  />
                  <Form.Control.Feedback type="invalid">
                    {validate.email}
                  </Form.Control.Feedback>
                </Form.Group>

                <Form.Group className="mb-4">
                  <Form.Label className="fw-semibold">Password</Form.Label>
                  <Form.Control
                    type="password"
                    name="password"
                    placeholder="Password"
                    required
                    onChange={handleChange}
                    value={formData.password}
                    isInvalid={!!validate.password}
                  />
                  <Form.Control.Feedback type="invalid">
                    {validate.password}
                  </Form.Control.Feedback>
                </Form.Group>

                <div className="d-grid gap-2">
                  <Button variant="primary" type="submit" disabled={disable}>
                    Register
                  </Button>
                </div>
              </Form>
            </div>
            <p className="mt-2 text-center">
              Already have an account? <Link to="/login">Login</Link>
            </p>
          </div>
        </div>
      </div>

      <Footer />
    </div>
  );
};

export default Register;
