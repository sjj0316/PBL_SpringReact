import React, { useEffect, useState } from 'react';
import {
  Container,
  Paper,
  Typography,
  Avatar,
  Box,
  Grid,
  Button,
  TextField,
} from '@mui/material';
import { useSelector, useDispatch } from 'react-redux';
import { RootState } from '../../store';

interface UserProfile {
  username: string;
  email: string;
  avatar?: string;
  bio?: string;
}

const Profile: React.FC = () => {
  const [isEditing, setIsEditing] = useState(false);
  const [profile, setProfile] = useState<UserProfile>({
    username: '',
    email: '',
    bio: '',
  });

  const user = useSelector((state: RootState) => state.auth.user);

  useEffect(() => {
    if (user) {
      setProfile({
        username: user.username,
        email: user.email,
        bio: user.bio || '',
        avatar: user.avatar,
      });
    }
  }, [user]);

  const handleEdit = () => {
    setIsEditing(true);
  };

  const handleSave = () => {
    // TODO: Implement profile update logic
    setIsEditing(false);
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setProfile(prev => ({
      ...prev,
      [name]: value,
    }));
  };

  return (
    <Container maxWidth="md">
      <Paper elevation={3} sx={{ p: 4, mt: 4 }}>
        <Grid container spacing={4}>
          <Grid item xs={12} md={4} sx={{ textAlign: 'center' }}>
            <Avatar
              src={profile.avatar}
              sx={{ width: 150, height: 150, mx: 'auto', mb: 2 }}
            />
            <Typography variant="h5" gutterBottom>
              {profile.username}
            </Typography>
            <Typography color="textSecondary" gutterBottom>
              {profile.email}
            </Typography>
          </Grid>
          <Grid item xs={12} md={8}>
            <Box sx={{ mb: 3 }}>
              <Typography variant="h6" gutterBottom>
                Profile Information
              </Typography>
              {isEditing ? (
                <>
                  <TextField
                    fullWidth
                    label="Username"
                    name="username"
                    value={profile.username}
                    onChange={handleChange}
                    margin="normal"
                  />
                  <TextField
                    fullWidth
                    label="Bio"
                    name="bio"
                    value={profile.bio}
                    onChange={handleChange}
                    margin="normal"
                    multiline
                    rows={4}
                  />
                  <Button
                    variant="contained"
                    color="primary"
                    onClick={handleSave}
                    sx={{ mt: 2, mr: 1 }}
                  >
                    Save
                  </Button>
                  <Button
                    variant="outlined"
                    onClick={() => setIsEditing(false)}
                    sx={{ mt: 2 }}
                  >
                    Cancel
                  </Button>
                </>
              ) : (
                <>
                  <Typography paragraph>{profile.bio || 'No bio provided'}</Typography>
                  <Button
                    variant="contained"
                    color="primary"
                    onClick={handleEdit}
                    sx={{ mt: 2 }}
                  >
                    Edit Profile
                  </Button>
                </>
              )}
            </Box>
          </Grid>
        </Grid>
      </Paper>
    </Container>
  );
};

export default Profile; 