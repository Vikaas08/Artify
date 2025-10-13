import cors from "cors";
import axios from "axios";
import cookieParser from "cookie-parser";
import jwt from "jsonwebtoken";
import bcrypt from "bcrypt";
import crypto from "crypto";
import mongoose from "mongoose";
import path from "path";
import express from "express";
import env from "dotenv";
import { dirname } from "path";
import { fileURLToPath } from "url";
const __dirname = dirname(fileURLToPath(import.meta.url));

const app = express();
env.config();
app.use(express.urlencoded({ extended: true }));
app.use(cookieParser());
app.use(express.json());
app.use(
  cors({
    origin: "https://hw3-cs571-spring-25.uw.r.appspot.com",
    // origin: "https:localhost:5173",
    credentials: true,
  })
);

const secret = process.env.JWT_SECRET_KEY;
const uri = process.env.MONGODB_URI;
mongoose
  .connect(uri)
  .then(() => console.log("Connected to MongoDB"))
  .catch((err) => console.error("MongoDB connection error:", err));

const userSchema = new mongoose.Schema({
  fullname: { type: String, required: true },
  email: { type: String, unique: true, required: true },
  password: { type: String, required: true },
  profileImageUrl: String,
  favorites: [
    {
      id: { type: String, required: true },
      fullname: { type: String, required: true },
      dob: { type: String },
      dod: { type: String },
      nationality: { type: String },
      profileImageUrl: { type: String },
      timestamp: { type: Date, default: Date.now },
    },
  ],
});

const User = mongoose.model("User", userSchema);

app.post("/api/register", (req, res) => {
  // console.log("Register form recieved at server", req.body);
  const { name, email, password } = req.body;

  User.findOne({ email: email })
    .then((foundUser) => {
      if (foundUser) {
        console.log("User already exists!!");
        res.status(204).json("User already exists!!");
      } else {
        bcrypt.hash(password, 11).then((hash) => {
          const newUser = new User({
            fullname: name,
            email: email,
            password: hash,
            profileImageUrl: getGravatarUrl(email, 80),
          });
          newUser
            .save()
            .then((users) => {
              // console.log("User registered successfully", users);
              res.status(200).json("User registered successfully");
            })
            .catch((err) => console.log(err));
        });
      }
    })
    .catch((err) => console.log(err));
});

app.post("/api/login", (req, res) => {
  // console.log("Login form recieved at server", req.body);
  const { email, password } = req.body;

  User.findOne({ email: email })
    .then((foundUser) => {
      if (foundUser) {
        bcrypt.compare(password, foundUser.password).then((result) => {
          if (result == true) {
            // console.log("User login successful", foundUser);

            const token = jwt.sign(
              {
                id: foundUser._id,
                fullname: foundUser.fullname,
                email: foundUser.email,
                profileImageUrl: foundUser.profileImageUrl,
              },
              secret,
              { expiresIn: "1h" }
            );

            // const isMobile = req.headers['user-agent'].includes('Mobile');
            const isMobile = req.headers["sec-ch-ua-mobile"] === "?1";
            if (isMobile) {
              res.json({ token });
            } else {
              res.cookie("token", token, {
                httpOnly: false,
                secure: false,
                sameSite: "None",
                expires: new Date(Date.now() + 3600000),
              });
            }

            res.json({
              token,
              id: foundUser._id,
              email: foundUser.email,
              fullname: foundUser.fullname,
              profileImageUrl: foundUser.profileImageUrl,
            });
          } else res.status(401).json("Incorrect Password");
        });
      } else {
        res.json("User not found!!");
      }
    })
    .catch((err) => console.log(err));
});

app.get("/api/favourites", jwtCookieAuth, async (req, res) => {
  const userId = req.user.id;
  try {
    const foundUser = await User.findById(userId);
    if (foundUser) {
      res.status(200).json(foundUser.favorites);
    } else {
      res.status(404).json("User not found");
    }
  } catch (err) {
    res.status(500).json("Error retreiving user");
  }
});

app.post("/api/favourites", jwtCookieAuth, async (req, res) => {
  const {
    id,
    fullname,
    dob,
    dod,
    nationality,
    profileImageUrl,
    timestamp,
    action,
  } = req.body;

  try {
    const foundUser = await User.findById(req.user.id);
    if (foundUser) {
      if (action === "add") {
        const favouriteExists = foundUser.favorites.some(
          (favorite) => favorite.id === id
        );

        if (favouriteExists) {
          return res.status(400).json("Favourite already exists");
        }

        const newFavorite = {
          id,
          fullname,
          dob,
          dod,
          nationality,
          profileImageUrl,
          timestamp,
        };
        foundUser.favorites.push(newFavorite);
      } else if (action === "remove") {
        foundUser.favorites = foundUser.favorites.filter(
          (fav) => fav.id !== id
        );
      }

      foundUser
        .save()
        .then((updatedUser) => {
          // console.log("Favorites updated successfully", updatedUser);
          res.status(200).json("Favorites updated successfully");
        })
        .catch((err) => console.log(err));
    } else {
      res.status(404).json("User not found");
    }
  } catch (err) {
    res.status(500).json("Error retreiving user");
  }
});

app.get("/api/verify", (req, res) => {
  const token = req.cookies.token;
  if (!token) return res.status(401).json("Unauthorized");

  try {
    const decoded = jwt.verify(token, SECRET);
    res.send({ user: decoded });
  } catch (err) {
    res.status(401).json("Invalid Token");
  }
});

app.post("/api/logout", jwtCookieAuth, (req, res) => {
  console.log("server logout called");
  res.clearCookie("token");
  res.json({ message: "Logged out" });
});

app.post("/api/delete", (req, res) => {
  const { email } = req.body;

  User.findOneAndDelete({ email: email })
    .then((deleteUser) => {
      if (deleteUser) {
        res.status(200).json({ message: "User account deleted successfully" });
      } else {
        res.status(204).json({ message: "User not found!!" });
      }
    })
    .catch((err) => console.log(err));
});

// Artsy API route
app.get("/api/search", async (req, res) => {
  // console.log("Search query recieved at server", req.body);
  let artist = req.query.userInput;
  try {
    const response = await axios({
      method: "GET",
      url: "https://api.artsy.net/api/search",
      params: {
        q: artist,
        size: 10,
        type: "artist",
      },
      headers: {
        "X-Xapp-Token": process.env.ARTSY_TOKEN,
      },
    });
    const result = response.data;
    res.json(result);
  } catch (error) {
    console.log("Failed to get search results", error);
  }
});

app.post("/api/about", async (req, res) => {
  //get artist uid from each card
  let artist_uid = req.body.uid;
  try {
    const response = await axios({
      method: "GET",
      url: `https://api.artsy.net/api/artists/${artist_uid}`,
      headers: {
        "X-Xapp-Token": process.env.ARTSY_TOKEN,
      },
    });
    res.json(response.data);
  } catch (error) {
    console.log("Failed to get artist details", error);
  }
});

app.post("/api/similar", async (req, res) => {
  let artist_uid = req.body.uid;
  try {
    const response = await axios({
      method: "GET",
      url: "https://api.artsy.net/api/artists",
      params: {
        similar_to_artist_id: artist_uid,
      },
      headers: {
        "X-Xapp-Token": process.env.ARTSY_TOKEN,
      },
    });
    res.json(response.data);
  } catch (error) {
    console.log("Failed to get artist details", error);
  }
});

app.post("/api/artworks", async (req, res) => {
  let artist_uid = req.body.uid;
  try {
    const response = await axios({
      method: "GET",
      url: "https://api.artsy.net/api/artworks",
      params: {
        artist_id: artist_uid,
        size: 10,
      },
      headers: {
        "X-Xapp-Token": process.env.ARTSY_TOKEN,
      },
    });
    res.json(response.data);
  } catch (error) {
    console.log("Failed to get artist artworks", error);
  }
});

app.post("/api/category", async (req, res) => {
  let artwork_uid = req.body.id;
  try {
    const response = await axios({
      method: "GET",
      url: "https://api.artsy.net/api/genes",
      params: {
        artwork_id: artwork_uid,
      },
      headers: {
        "X-Xapp-Token": process.env.ARTSY_TOKEN,
      },
    });
    res.json(response.data);
  } catch (error) {
    console.log("Failed to get artwork categories", error);
  }
});

async function artsyAuth() {
  const artsy_id = process.env.ARTSY_CLIENT_ID;
  const artsy_secret = process.env.ARTSY_CLIENT_SECRET;
  try {
    const response = await axios({
      method: "POST",
      url: "https://api.artsy.net/api/tokens/xapp_token",
      params: {
        client_id: artsy_id,
        client_secret: artsy_secret,
      },
    });
    const result = response.data;
    // console.log("authenticated");
    const token = result.token;
    const expires = result.expires_at;
  } catch (error) {
    console.log("Failed to authenticate", error);
  }
}

function getGravatarUrl(email, size = 80) {
  const trimmedEmail = email.trim().toLowerCase();
  const hash = crypto.createHash("sha256").update(trimmedEmail).digest("hex");
  return `https://www.gravatar.com/avatar/${hash}?s=${size}&d=robohash`;
}

function jwtCookieAuth(req, res, next) {
  let token;
  const isMobile = req.headers["sec-ch-ua-mobile"] === "?1";
  if (isMobile) {
    const userId = req.headers["authorization"];
    // console.log(req.headers);
    token = userId.split("Bearer ")[1];
  } else {
    token = req.cookies.token;
  }

  // console.log(req.cookies.token);
  if (!token) res.sendStatus(401).json("Unauthorized");

  jwt.verify(token, secret, (err, decoded) => {
    if (err) {
      return res.sendStatus(401).json("Invalid token");
    } else {
      req.user = decoded;
      next();
    }
  });
}

app.use(express.static(path.join(__dirname, "dist")));
app.get("*", (req, res) => {
  res.sendFile(path.join(__dirname, "dist", "index.html"));
});

app.get("/", (req, res) => {
  res.send("Hello from App Engine!");
});

const PORT = process.env.PORT || 8080;
app.listen(PORT, () => {
  console.log(`Server listening on port ${PORT}...`);
});
